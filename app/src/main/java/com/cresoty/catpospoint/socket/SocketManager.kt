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
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private var job: Job = Job()

    private var ip: String = ""
    private var port: Int = 0
    private val ACK_ARRAY: ByteArray = ByteArray(3) { Val.COMM_ACK }
    private val NAK_ARRAY: ByteArray = ByteArray(3) { Val.COMM_NAK }

    var telegramReceiver: (String, ByteArray) -> Unit = { _, _ -> }

    private var serverChannel: ServerSocketChannel? = null
    private var selector: Selector? = null

    // 단일 클라이언트만 허용(필요 시 Map<SocketChannel, ConnCtx>로 확장)
    private var clientKey: SelectionKey? = null

    private data class ConnCtx(
        val readBuf: ByteBuffer = ByteBuffer.allocateDirect(8 * 1024),
        val recvBaos: ByteArrayOutputStream = ByteArrayOutputStream(),
        val writeQueue: ArrayDeque<PendingWrite> = ArrayDeque(),
    )

    private data class PendingWrite(
        val buf: ByteBuffer,
        val onComplete: ((written: Int) -> Unit)? = null,
        var writtenTotal: Int = 0
    )

    private data class AckDetectResult(
        val hasAck: Boolean,
        val ackLen: Int,          // 0, 3, 6
        val newBuffer: ByteArray  // ACK 제거 후 남은 데이터
    )

    /**
     * buffer 안에 0x06이 연속으로 3개 이상(최대 6개) 존재하면 ACK로 판단.
     * 발견된 ACK(3~6바이트)를 buffer에서 제거해서 반환.
     */
    private fun stripAckIfPresent(buffer: ByteArray): AckDetectResult {
        val ACK = 0x06.toByte()

        var i = 0
        while (i < buffer.size) {
            if (buffer[i] != ACK) {
                i++
                continue
            }

            // 연속 ACK 길이 계산
            var j = i
            while (j < buffer.size && buffer[j] == ACK) j++
            val run = j - i

            if (run >= 3) {
                // 3 이상이면 ACK로 인정, 길이는 6까지(그 이상도 올 수 있으면 정책에 맞게 조정)
                val ackLen = if (run >= 6) 6 else 3

                // ACK 제거: [0..i) + [i+ackLen..end)
                val out = ByteArray(buffer.size - ackLen)
                System.arraycopy(buffer, 0, out, 0, i)
                System.arraycopy(buffer, i + ackLen, out, i, buffer.size - (i + ackLen))

                return AckDetectResult(
                    hasAck = true,
                    ackLen = ackLen,
                    newBuffer = out
                )
            }

            // run이 1~2면 ACK가 아니므로 다음 위치로
            i = j
        }

        return AckDetectResult(false, 0, buffer)
    }

    fun start(): Job = launch {
        // 재시작 루프(원하시면 제거 가능)
        while (isActive) {
            try {
                runServerLoop()
            } catch (e: CancellationException) {
                // 정상 종료
                break
            } catch (e: Exception) {
                Log.e("SocketDebug", "runServerLoop 예외 발생: ${e.message}", e)
                e.printStackTrace()
            } finally {
                Log.d("SocketDebug", "safeCloseAll 호출")
                safeCloseAll()
            }
            // 오류 재시작 텀
            delay(1000)
        }
    }

    private fun runServerLoop() {
        Log.d("SocketDebug", "runServerLoop 시작 - 포트 52391 바인딩 시도")
        selector = Selector.open()
        serverChannel = ServerSocketChannel.open().apply {
            configureBlocking(false)
            socket().reuseAddress = true
            bind(InetSocketAddress(52391))
            Log.d("SocketDebug", "포트 52391 바인딩 성공")

            register(selector, SelectionKey.OP_ACCEPT)
        }

        val sel = selector!!

        while (sel.isOpen) {
            // timeout을 주면 cancel 시에도 비교적 빠르게 빠져나옵니다.
            val ready = sel.select(500)
            if (!isActive) throw CancellationException()

            if (ready == 0) {
                // 타임아웃: 할 일 없으면 continue
                continue
            }

            val it = sel.selectedKeys().iterator()
            while (it.hasNext()) {
                val key = it.next()
                it.remove()

                if (!key.isValid) {
                    closeKey(key)
                    continue
                }

                try {
                    // else-if 금지: 동시에 true일 수 있음
                    if (key.isAcceptable) onAccept(key)
                    if (key.isReadable) onRead(key)
                    if (key.isWritable) onWrite(key)
                } catch (e: CancelledKeyException) {
                    closeKey(key)
                } catch (e: IOException) {
                    // IO 예외는 연결 단절 케이스가 많음
                    closeKey(key)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 치명적이지 않으면 연결만 정리
                    closeKey(key)
                }
            }
        }
    }

    private fun onAccept(key: SelectionKey) {
        val ssc = key.channel() as ServerSocketChannel
        val sc = ssc.accept() ?: run {
            Log.w("SocketDebug", "accept() 반환값이 null")
            return
        }
        sc.configureBlocking(false)

        val addr = sc.socket().remoteSocketAddress as? InetSocketAddress
        ip = addr?.address?.hostAddress ?: addr?.hostString ?: ""
        port = addr?.port ?: 0
        Log.d("SocketDebug", "클라이언트 연결됨 - ip: $ip, port: $port")

        // 단일 클라이언트만 허용: 기존 있으면 끊고 새로 받기(정책)
        clientKey?.let { closeKey(it) }

        val ctx = ConnCtx()
        val newKey = sc.register(selector, SelectionKey.OP_READ, ctx)
        clientKey = newKey
    }

    private fun onRead(key: SelectionKey) {
        Log.e("SocketDebug", "onRead 진입함")
        val sc = key.channel() as SocketChannel
        val ctx = key.attachment() as ConnCtx

        val buf = ctx.readBuf
        buf.clear()

        val n = sc.read(buf)

        when {
            n > 0 -> {
                buf.flip()
                val bytes = ByteArray(buf.remaining())
                buf.get(bytes)

                // 누적(프레이밍 필요 시 여기서 패킷 단위로 잘라서 처리)
                ctx.recvBaos.write(bytes)

                val receivedString = String(bytes, Charsets.UTF_8)

                Log.d("SocketDebug", "데이터 수신 (${n}bytes): ${bytes.byte2String()}")
                Log.d("SocketDebug", "데이터 수신 한글 (${n}bytes): $receivedString")

                // 지금은 사용자가 하던 방식처럼 "일단 모았다가 처리" 형태로 예시
                // 다만 이 방식은 패킷 경계가 확실하지 않으면 위험합니다.
                val data = ctx.recvBaos.toByteArray()
                if (data.isNotEmpty()) {
                    val cmd = parseCatposCommand(data).ifEmpty { findCommand(data.size, data) }

                    Log.d("SocketDebug", "findCommand 결과: '$cmd' (data size: ${data.size})")


                    telegramReceiver(cmd, data)

                    ctx.recvBaos.reset()
                    if (cmd != Val.CATPOS) send(ACK_ARRAY)
                }
            }

            n == 0 -> {
                // non-blocking에서 0은 "지금은 더 없음"
                return
            }

            else -> {
                Log.d("SocketDebug", "클라이언트 연결 종료")
                // n == -1 : 상대가 정상 종료(EOF)
                closeKey(key)
            }
        }
    }

    private fun onWrite(key: SelectionKey) {
        val sc = key.channel() as? SocketChannel ?: run {
            Log.w("@#@#", "onWrite: SocketChannel null")
            return
        }

        val ctx = key.attachment() as? ConnCtx ?: run {
            Log.w("@#@#", "onWrite: ConnCtx null")
            return
        }

        while (ctx.writeQueue.isNotEmpty()) {
            val head = ctx.writeQueue.firstOrNull() ?: break

            try {
                val n = sc.write(head.buf)
                if (n > 0) head.writtenTotal += n

                if (head.buf.hasRemaining()) break
                else {
                    ctx.writeQueue.removeFirst()
                    head.onComplete?.invoke(head.writtenTotal)
                }
            } catch (e: IOException) {
                Log.e("@#@#", "onWrite IOException", e)
                closeKey(key)
                break
            } catch (e: Exception) {
                Log.e("@#@#", "onWrite Exception", e)
                closeKey(key)
                break
            }
        }

        try {
            if (ctx.writeQueue.isEmpty()) {
                key.interestOps(key.interestOps() and SelectionKey.OP_WRITE.inv() or SelectionKey.OP_READ)
            }
        } catch (_: Exception) {
        }
    }

    fun send(data: ByteArray?, onComplete: ((written: Int) -> Unit)? = null) {
        if (data == null || data.isEmpty()) {
            Log.w("@#@#", "send 호출 시 data null 또는 empty")
            return
        }

        val key = clientKey ?: run {
            Log.e("@#@#", "send 실패: clientKey null")
            return
        }

        if (!key.isValid) {
            Log.e("@#@#", "send 실패: key invalid")
            return
        }

        val ctx = key.attachment() as? ConnCtx ?: run {
            Log.e("@#@#", "send 실패: ConnCtx null")
            return
        }

        Log.d("@#@#", "send telegram : ${data.byte2String()}")
        Log.d("@#@#", "writeQueue size before add: ${ctx.writeQueue.size}")

        ctx.writeQueue.add(PendingWrite(ByteBuffer.wrap(data), onComplete))

        Log.d("@#@#", "writeQueue size after add: ${ctx.writeQueue.size}")

        try {
            key.interestOps(key.interestOps() or SelectionKey.OP_WRITE)
            selector?.wakeup()
        } catch (e: Exception) {
            Log.e("@#@#", "send: key.interestOps 실패", e)
        }
    }

    private fun closeKey(key: SelectionKey) {
        try {
            key.cancel()
        } catch (_: Exception) {
        }
        try {
            (key.channel() as? Channel)?.close()
        } catch (_: Exception) {
        }
        if (key == clientKey) clientKey = null
    }

    private fun safeCloseAll() {
        // client
        clientKey?.let { closeKey(it) }
        clientKey = null

        // server & selector
        try {
            serverChannel?.close()
        } catch (_: Exception) {
        }
        serverChannel = null
        try {
            selector?.close()
        } catch (_: Exception) {
        }
        selector = null
    }

    // CATPOS 파이프 형식 파싱: "CAT|..." → "CAT"
    private fun parseCatposCommand(data: ByteArray): String {
        val pipeIdx = data.indexOf('|'.code.toByte())
        if (pipeIdx <= 0) return ""
        val candidate = String(data, 0, pipeIdx, Charsets.UTF_8).trim()
        return if (candidate == Val.CATPOS) candidate else ""
    }

    private fun findCommand(
        length: Int,
        data: ByteArray
    ): String {
        var pktLen: Int = 0
        var pktChkPos: Int = 0
        var rcvPktLen: Int = 0

        if (length < 5) return ""

        val stxPosition: Int = data.findAsciiControlChar(Val.COMM_STX)

        if (stxPosition == 0) {
            try {
                while (pktChkPos + 3 < length) {
                    val slice = data.slice(pktChkPos until minOf(pktChkPos + 5, length))
                    val digitCount = slice.takeWhile { b -> isDigit(b) }.size

                    if (digitCount == 4) {
                        break
                    }

                    pktChkPos++
                }

                pktLen = String(data, pktChkPos, 4).toInt()

                rcvPktLen = stxPosition + pktLen //(STX ~ ETX까지의 길이, CRC제외)

                if (pktChkPos >= length || rcvPktLen + 1 > length) {
                    return ""
                }

                if (data[rcvPktLen - 1] == Val.COMM_ETX) {
                    val trm = String(data, pktChkPos + 4, 3)
                    if (trm == Val.TERMINAL_FLAG) {
                        val cmd = String(data, pktChkPos + 7, 3)
                        return cmd
                    }
                } else {
                    return ""
                }
            } catch (_: Exception) {
                return ""
            }

        } else {
            return ""
        }

        return ""
    }


}