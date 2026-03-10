package com.cresoty.catpospoint.socket

import android.util.Log
import com.cresoty.catpospoint.Val
import com.cresoty.catpospoint.byte2String
import com.cresoty.catpospoint.findAsciiControlChar
import com.cresoty.catpospoint.isDigit
import com.cresoty.catpospoint.socket.protocol.CatposMessage
import com.cresoty.catpospoint.socket.protocol.CatposParser
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

class SocketManager @Inject constructor() : CoroutineScope {

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    // Socket → Application 전달
    var telegramReceiver: (cmd: String, data: ByteArray) -> Unit = { _, _ -> }
    var pcTelegramReceiver: (CatposMessage) -> Unit = { }

    private var selector: Selector? = null
    private var serverChannel: ServerSocketChannel? = null

    /** 항상 마지막 클라이언트만 유지 */
    private var clientKey: SelectionKey? = null

    private val ack: ByteArray = ByteArray(3) { Val.COMM_ACK }

    // ── 내부 타입 ─────────────────────────────────────

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

    // ── 서버 시작 ─────────────────────────────────────

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

    // ── 전송 ─────────────────────────────────────────

    fun send(data: ByteArray, onComplete: ((written: Int) -> Unit)? = null) {

        if (data.isEmpty()) {
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

        Log.d(TAG, "send: ${data.byte2String()} (큐:${ctx.writeQueue.size})")

        ctx.writeQueue.add(PendingWrite(data, onComplete))

        runCatching {
            key.interestOps(key.interestOps() or SelectionKey.OP_WRITE)
            selector?.wakeup()
        }.onFailure {
            Log.e(TAG, "send: interestOps 설정 실패", it)
        }
    }

    // ── 서버 루프 ───────────────────────────────────

    private fun runServerLoop() {

        Log.d(TAG, "서버 시작 - 포트 $SERVER_PORT")

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

            if (ready == 0) continue

            val iterator = sel.selectedKeys().iterator()

            while (iterator.hasNext()) {

                val key = iterator.next().also { iterator.remove() }

                if (!key.isValid) {
                    closeKey(key)
                    continue
                }

                runCatching { handleKey(key) }
                    .onFailure { e ->
                        when (e) {
                            is CancelledKeyException,
                            is IOException -> closeKey(key)

                            else -> {
                                e.printStackTrace()
                                closeKey(key)
                            }
                        }
                    }
            }
        }
    }

    private fun handleKey(key: SelectionKey) {

        if (key.isAcceptable) onAccept(key)
        if (key.isReadable) onRead(key)
        if (key.isWritable) onWrite(key)
    }

    // ── 이벤트 처리 ─────────────────────────────────

    private fun onAccept(key: SelectionKey) {

        val sc = (key.channel() as ServerSocketChannel).accept() ?: return

        sc.configureBlocking(false)

        val addr = sc.socket().remoteSocketAddress as? InetSocketAddress

        Log.d(TAG, "클라이언트 연결 시도: ${addr?.hostString}:${addr?.port}")

        // 기존 연결 종료
        clientKey?.let {
            Log.d(TAG, "기존 연결 종료")
            closeKey(it)
        }

        // 새 연결 등록
        clientKey = sc.register(selector, SelectionKey.OP_READ, ConnCtx())

        Log.d(TAG, "새 클라이언트 연결 완료")
    }

    private fun onRead(key: SelectionKey) {

        val sc = key.channel() as SocketChannel

        val ctx = key.attachment() as ConnCtx

        ctx.readBuf.clear()

        val n = sc.read(ctx.readBuf)

        when {
            n > 0 -> processReceivedBytes(ctx, n)

            n == 0 -> return

            else -> {
                Log.d(TAG, "클라이언트 연결 종료")
                closeKey(key)
            }
        }
    }

    private fun processReceivedBytes(ctx: ConnCtx, n: Int) {

        ctx.readBuf.flip()

        val bytes = ByteArray(ctx.readBuf.remaining()).also {
            ctx.readBuf.get(it)
        }

        ctx.recvBuf.write(bytes)

        Log.d(TAG, "수신 (${n}bytes): ${bytes.toReadable()}")

        val data = ctx.recvBuf.toByteArray()

        if (data.isEmpty()) return

        val catposMsg = CatposParser.parse(data)

        if (catposMsg != null && catposMsg.system == Val.CATPOS) {

            Log.d(TAG, "PC 전문 수신: ${catposMsg.command}")

            pcTelegramReceiver(catposMsg)

        } else {

            val cmd = findCommand(data)

            Log.d(TAG, "단말기 명령어: $cmd")

            telegramReceiver(cmd, data)

            send(ack)   // 단말기만 ACK
        }

        ctx.recvBuf.reset()
    }

    private fun onWrite(key: SelectionKey) {

        val sc = key.channel() as? SocketChannel ?: return

        val ctx = key.attachment() as? ConnCtx ?: return

        while (ctx.writeQueue.isNotEmpty()) {

            val head = ctx.writeQueue.first()

            val n = runCatching { sc.write(head.buf) }
                .onFailure {
                    Log.e(TAG, "쓰기 오류", it)
                    closeKey(key)
                    return
                }
                .getOrDefault(0)

            if (n > 0) head.written += n

            if (head.buf.hasRemaining()) break

            ctx.writeQueue.removeFirst()

            head.onComplete?.invoke(head.written)
        }

        if (ctx.writeQueue.isEmpty()) {

            runCatching {

                key.interestOps(
                    key.interestOps()
                            and SelectionKey.OP_WRITE.inv()
                            or SelectionKey.OP_READ
                )
            }
        }
    }

    // ── 연결 관리 ─────────────────────────────────

    private fun closeKey(key: SelectionKey) {

        runCatching { key.cancel() }

        runCatching { (key.channel() as? Channel)?.close() }

        if (key == clientKey) clientKey = null
    }

    private fun safeCloseAll() {

        clientKey?.let { closeKey(it) }

        clientKey = null

        runCatching { serverChannel?.close() }.also { serverChannel = null }

        runCatching { selector?.close() }.also { selector = null }
    }

    // ── 전문 파싱 ─────────────────────────────────

    private fun parseCatposCommand(data: ByteArray): String {

        val pipeIdx = data.indexOf('|'.code.toByte())

        if (pipeIdx <= 0) return ""

        val candidate = String(data, 0, pipeIdx, Charsets.UTF_8).trim()

        return if (candidate == Val.CATPOS) candidate else ""
    }

    private fun findCommand(data: ByteArray): String {

        if (data.size < 5) return ""

        if (data.findAsciiControlChar(Val.COMM_STX) != 0) return ""

        return try {

            var pos = 0

            while (pos + 3 < data.size) {

                val digitCount = data
                    .slice(pos until minOf(pos + 5, data.size))
                    .takeWhile { isDigit(it) }
                    .size

                if (digitCount == 4) break

                pos++
            }

            val pktLen = String(data, pos, 4).toInt()

            val rcvPktLen = pktLen

            if (pos >= data.size || rcvPktLen + 1 > data.size) return ""

            if (data[rcvPktLen - 1] != Val.COMM_ETX) return ""

            val trm = String(data, pos + 4, 3)

            if (trm != Val.TERMINAL_FLAG) return ""

            String(data, pos + 7, 3)

        } catch (_: Exception) {
            ""
        }
    }

    fun ByteArray.toReadable(): String =
        joinToString(" ") {
            when (it.toInt() and 0xFF) {
                0x02 -> "[STX]"
                0x03 -> "[ETX]"
                0x06 -> "[ACK]"
                0x15 -> "[NAK]"
                0x1C -> "[FS]"
                else -> String.format("%02X", it)
            }
        }

    companion object {

        private const val TAG = "SocketManager"

        private const val SERVER_PORT = 52391

        private const val SELECT_TIMEOUT_MS = 500L

        private const val RESTART_DELAY_MS = 1000L

        private const val BUFFER_SIZE = 8 * 1024
    }
}