package com.cresoty.catpospoint.data.socket

import com.cresoty.catpospoint.Val
import com.cresoty.catpospoint.byte2String
import com.cresoty.catpospoint.domain.socket.SocketEvent
import com.cresoty.catpospoint.domain.socket.SocketEventRepository
import com.cresoty.catpospoint.socket.SocketManager
import com.cresoty.catpospoint.splitTelegram
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SocketManager 의 콜백을 SharedFlow<SocketEvent> 로 변환하는 단일 구독 지점.
 *
 * - 싱글톤이므로 앱 수명 동안 콜백은 한 번만 등록되고 서버도 한 번만 시작된다.
 * - MainViewModel(구 아키텍처)과 AppViewModel(신 아키텍처) 모두 events 를 collect 한다.
 * - SocketManager 의 var 콜백은 마지막 할당이 이기므로, 여기서만 설정해야 한다.
 */
@Singleton
class SocketEventRepositoryImpl @Inject constructor(
    private val socketManager: SocketManager,
) : SocketEventRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<SocketEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val events: SharedFlow<SocketEvent> = _events.asSharedFlow()

    init {
        // 소켓 서버 시작 (싱글톤 → 앱 수명 동안 한 번만)
        socketManager.start()

        // 단말기 전문 콜백 → SocketEvent 변환
        socketManager.telegramReceiver = { cmd, data ->
            val list = data.splitTelegram(Val.COMM_FS).map { it.byte2String() }
            val event: SocketEvent? = when (cmd) {
                Val.TERMINAL_COMMAND_001 -> SocketEvent.TerminalEarnPointSingle(list)
                Val.TERMINAL_COMMAND_002 -> SocketEvent.TerminalEarnPointComplex(list)
                Val.TERMINAL_COMMAND_003 -> SocketEvent.TerminalUsePoint(list)
                else -> null
            }
            event?.let { scope.launch { _events.emit(it) } }
        }

        // 캣포스(PC) 전문 콜백 → SocketEvent 변환
        socketManager.pcTelegramReceiver = { msg ->
            val event: SocketEvent? = when (msg.command) {
                Val.CATPOS_CONNECT             -> SocketEvent.CatConnect
                Val.CATPOS_NUM                 -> SocketEvent.CatRequestNum
                Val.CATPOS_CST                 -> SocketEvent.CatRequestCustomer
                Val.CATPOS_DISCONNECT          -> SocketEvent.CatDisconnect
                Val.CATPOS_EARN_POINT          -> SocketEvent.CatEarnPointSingle(msg.fields)
                Val.CATPOS_EARN_POINT_COMPLEX  -> SocketEvent.CatEarnPointComplex(msg.fields)
                Val.CATPOS_USE_POINT_NO_CUSTOMER   -> SocketEvent.CatUsePointNoCustomer(msg.fields)
                Val.CATPOS_USE_POINT_WITH_CUSTOMER -> SocketEvent.CatUsePointWithCustomer(msg.fields)
                else -> null
            }
            event?.let { scope.launch { _events.emit(it) } }
        }
    }
}
