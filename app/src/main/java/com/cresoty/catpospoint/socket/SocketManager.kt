package com.cresoty.catpospoint.socket

import android.util.Log
import com.cresoty.catpospoint.Val
import com.cresoty.catpospoint.byte2String
import com.cresoty.catpospoint.findAsciiControlChar
import com.cresoty.catpospoint.isDigit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.CancelledKeyException
import java.nio.channels.Channel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

class SocketManager @Inject constructor() : CoroutineScope {

    // ── Coroutine ──────────────────────────────────────────────────────────

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job

    // ── 상태 ───────────────────────────────────────────────────────────────

    // Socket → Application 전달
    var telegramReceiver: (cmd: String, data: ByteArray) -> Unit = { _, _ -> }

    private var selector: Selector? = null
    private var serverChannel: ServerSocketChannel? = null

    /** 단일 클라이언트만 허용.*/
    private var clientKey: SelectionKey? = null

    /** 활성 연결 중 새로 들어온 연결 — 첫 메시지 수신 후 CATPOS/단말기 판별 */
    private var pendingKey: SelectionKey? = null

    /**
     * 작업 진행 중 여부 — ViewModel에서 제어
     * true이면 신규 CATPOS 연결 거부
     * (단말기 포인트 적립일 경우 전문 전송 후 소켓을 끊으므로 clientKey만으로는 판별 불가)
     */
    @Volatile var isBusy: Boolean = false

    private val ack: ByteArray = ByteArray(3) { Val.COMM_ACK }

    // ── 내부 타입 ──────────────────────────────────────────────────────────

    private data class ConnCtx(
        val readBuf: ByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE),
        val recvBuf: ByteArrayOutputStream = ByteArrayOutputStream(),
        val writeQueue: ArrayDeque<PendingWrite> = ArrayDeque(),
    )

    private class PendingWrite(
        data: ByteArray,
        val onComplete: ((written: Int) -> Unit)? = null,
    ) {
        val buf: ByteBuffer = ByteBuffer.wrap(data)
        var written: Int = 0
    }

    // ── 공개 API ───────────────────────────────────────────────────────────

    fun start(): Job = launch {
        while (isActive) {
            try {
                runServerLoop()
            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                Log.e(TAG, "서버 루프 오류: ${e.message}", e)
            } finally {
                Log.d(TAG, "서버 루프 종료 - 리소스 정리")
                safeCloseAll()
            }
            delay(RESTART_DELAY_MS)
        }
    }


    /**
     * 전송 예약(writeQueue에 넣고 실제 소켓 전송 안 함)
     */
    fun send(data: ByteArray?, onComplete: ((written: Int) -> Unit)? = null) {
        if (data == null || data.isEmpty()) {
            Log.w(TAG, "send: 데이터 없음")
            return
        }
        val key = clientKey?.takeIf { it.isValid } ?: run {
            Log.e(TAG, "send: 유효한 클라이언트 없음")
            return
        }
        val ctx = key.attachment() as? ConnCtx ?: run {
            Log.e(TAG, "send: ConnCtx 없음")
            return
        }

        Log.d(TAG, "send: ${data.byte2String()} (큐: ${ctx.writeQueue.size})")
        ctx.writeQueue.add(PendingWrite(data, onComplete))

        runCatching {
            key.interestOps(key.interestOps() or SelectionKey.OP_WRITE)
            selector?.wakeup()
        }.onFailure { Log.e(TAG, "send: interestOps 설정 실패", it) }
    }

    // ── 서버 루프 ──────────────────────────────────────────────────────────

    private fun runServerLoop() {
        Log.d(TAG, "서버 시작 - 포트 $SERVER_PORT 바인딩 시도")
        selector = Selector.open()
        serverChannel = ServerSocketChannel.open().apply {
            configureBlocking(false)
            socket().reuseAddress = true
            bind(InetSocketAddress(SERVER_PORT))
            register(selector, SelectionKey.OP_ACCEPT)
            Log.d(TAG, "포트 $SERVER_PORT 바인딩 성공")
        }

        val sel = selector!!
        while (sel.isOpen) {
            val ready = sel.select(SELECT_TIMEOUT_MS)
            if (!isActive) throw CancellationException()
            // 테스트 후 제거 예정 - 소켓 상태 체크
            if (ready == 0) {
                val ch = clientKey?.let { it.isValid to (it.channel() as? SocketChannel)?.isConnected }
                Log.d(TAG, "소켓 상태 — clientKey=${clientKey?.isValid} connected=${ch?.second} isBusy=$isBusy")
                continue
            }
            //──────────────────────────────────────────────────────────

            if (ready == 0) continue

            val iterator = sel.selectedKeys().iterator()
            while (iterator.hasNext()) {
                val key = iterator.next().also { iterator.remove() }
                if (!key.isValid) { closeKey(key); continue }

                runCatching { handleKey(key) }
                    .onFailure { e ->
                        when (e) {
                            is CancelledKeyException, is IOException -> closeKey(key)
                            else -> { e.printStackTrace(); closeKey(key) }
                        }
                    }
            }
        }
    }

    private fun handleKey(key: SelectionKey) {
        // else-if 금지: accept/read/write가 동시에 true일 수 있음
        if (key.isAcceptable) onAccept(key)
        if (key.isReadable) onRead(key)
        if (key.isWritable) onWrite(key)
    }

    // ── 이벤트 핸들러 ─────────────────────────────────────────────────────

    /**
     * 클라이언트 연결 요청을 받을 때 호출
     */
    private fun onAccept(key: SelectionKey) {
        val sc = (key.channel() as ServerSocketChannel).accept() ?: run {
            Log.w(TAG, "accept(): 반환값 null")
            return
        }
        sc.configureBlocking(false)


        val addr = sc.socket().remoteSocketAddress as? InetSocketAddress
        Log.d(TAG, "클라이언트 연결 시도: ${addr?.hostString}:${addr?.port}")

//        // ── 테스트: 연결 즉시 강제 종료 ──
//        runCatching { sc.close() }.onSuccess { Log.d(TAG, "강제 종료 성공: ${addr?.hostString}:${addr?.port}") }
//            .onFailure { Log.e(TAG, "강제 종료 실패: ${it.message}") }
//        return
//        // ─────────────────────────────────

        if (isBusy) {
            // 작업 진행 중 — 첫 메시지를 읽어 CATPOS인지 단말기인지 판별
            pendingKey?.let { closeKey(it) }
            pendingKey = sc.register(selector, SelectionKey.OP_READ, ConnCtx())
            return
        }

        clientKey = sc.register(selector, SelectionKey.OP_READ, ConnCtx())
    }

    /**
     * 소켓에서 실제로 바이트를 읽는 단계
     */
    private fun onRead(key: SelectionKey) {
        if (key == pendingKey) { handlePendingRead(key); return }

        val sc = key.channel() as SocketChannel
        val ctx = key.attachment() as ConnCtx

        ctx.readBuf.clear()
        val n = sc.read(ctx.readBuf)

        when {
            n > 0  -> processReceivedBytes(ctx, n)
            n == 0 -> return  // non-blocking: 현재 읽을 데이터 없음
            else   -> { Log.d(TAG, "클라이언트 연결 종료 (EOF)"); closeKey(key) }
        }
    }

    /**
     * 읽어온 바이트를 누적 버퍼에 저장하는 단계
     */
    private fun handlePendingRead(key: SelectionKey) {
        val sc = key.channel() as SocketChannel
        val ctx = key.attachment() as ConnCtx

        ctx.readBuf.clear()
        val n = sc.read(ctx.readBuf)
        if (n <= 0) { closeKey(key); return }

        ctx.readBuf.flip()
        val bytes = ByteArray(ctx.readBuf.remaining()).also { ctx.readBuf.get(it) }

        if (parseCatposCommand(bytes) == Val.CATPOS) {
            Log.d(TAG, "CATPOS 신규 연결 거부 - 처리 중인 작업 있음")
            runCatching { sc.write(ByteBuffer.wrap("FAIL|다른작업중\r\n".toByteArray(Charsets.UTF_8))) }
        } else {
            Log.d(TAG, "단말기 신규 연결 거부 - 처리 중인 작업 있음")
        }
        closeKey(key)
    }

    /**
     * 누적된 데이터에서 실제 메시지를 파싱하는 단계
     */
    private fun processReceivedBytes(ctx: ConnCtx, n: Int) {
        ctx.readBuf.flip()
        val bytes = ByteArray(ctx.readBuf.remaining()).also { ctx.readBuf.get(it) }
        ctx.recvBuf.write(bytes)

        Log.d(TAG, "수신 (${n}bytes): ${bytes.byte2String()}")
        Log.d(TAG, "수신 문자열: ${String(bytes, Charsets.UTF_8)}")

        val data = ctx.recvBuf.toByteArray()
        if (data.isEmpty()) return

        val cmd = parseCatposCommand(data).ifEmpty { findCommand(data) }
        Log.d(TAG, "파싱 명령어: '$cmd' (${data.size}bytes)")

        telegramReceiver(cmd, data)
        ctx.recvBuf.reset()

        if (cmd != Val.CATPOS) send(ack)
    }


    /**
     * 실제 소켓에 write 하는 네트워크 이벤트 (실제 전송)
     */
    private fun onWrite(key: SelectionKey) {
        val sc = key.channel() as? SocketChannel ?: return
        val ctx = key.attachment() as? ConnCtx ?: return

        while (ctx.writeQueue.isNotEmpty()) {
            val head = ctx.writeQueue.first()

            val n = runCatching { sc.write(head.buf) }
                .onFailure { Log.e(TAG, "쓰기 오류", it); closeKey(key); return }
                .getOrDefault(0)

            if (n > 0) head.written += n
            if (head.buf.hasRemaining()) break

            ctx.writeQueue.removeFirst()
            head.onComplete?.invoke(head.written)
        }

        if (ctx.writeQueue.isEmpty()) {
            runCatching {
                key.interestOps(key.interestOps() and SelectionKey.OP_WRITE.inv() or SelectionKey.OP_READ)
            }
        }
    }

    // ── 연결 관리 ─────────────────────────────────────────────────────────

    private fun closeKey(key: SelectionKey) {
        runCatching { key.cancel() }
        runCatching { (key.channel() as? Channel)?.close() }
        if (key == clientKey) clientKey = null
        if (key == pendingKey) pendingKey = null
    }

    private fun safeCloseAll() {
        clientKey?.let { closeKey(it) }
        pendingKey?.let { closeKey(it) }
        clientKey = null
        pendingKey = null
        runCatching { serverChannel?.close() }.also { serverChannel = null }
        runCatching { selector?.close() }.also { selector = null }
    }

    // ── 전문 파싱 ─────────────────────────────────────────────────────────

    /** CATPOS 파이프 포맷 파싱: "CAT|..." → "CAT" */
    private fun parseCatposCommand(data: ByteArray): String {
        val pipeIdx = data.indexOf('|'.code.toByte())
        if (pipeIdx <= 0) return ""
        val candidate = String(data, 0, pipeIdx, Charsets.UTF_8).trim()
        return if (candidate == Val.CATPOS) candidate else ""
    }

    /** STX 기반 프레임 파싱: 패킷에서 3자리 명령어 추출 */
    private fun findCommand(data: ByteArray): String {
        if (data.size < 5) return ""
        if (data.findAsciiControlChar(Val.COMM_STX) != 0) return ""

        return try {
            var pos = 0
            while (pos + 3 < data.size) {
                val digitCount = data.slice(pos until minOf(pos + 5, data.size))
                    .takeWhile { isDigit(it) }.size
                if (digitCount == 4) break
                pos++
            }

            val pktLen = String(data, pos, 4).toInt()
            val rcvPktLen = pktLen  // stxPosition == 0 이므로 stxPosition + pktLen = pktLen

            if (pos >= data.size || rcvPktLen + 1 > data.size) return ""
            if (data[rcvPktLen - 1] != Val.COMM_ETX) return ""

            val trm = String(data, pos + 4, 3)
            if (trm != Val.TERMINAL_FLAG) return ""

            String(data, pos + 7, 3)
        } catch (_: Exception) {
            ""
        }
    }

    // ── 상수 ──────────────────────────────────────────────────────────────

    companion object {
        private const val TAG = "SocketManager"
        private const val SERVER_PORT = 52391
        private const val SELECT_TIMEOUT_MS = 500L
        private const val RESTART_DELAY_MS = 1000L
        private const val BUFFER_SIZE = 8 * 1024
    }
}
