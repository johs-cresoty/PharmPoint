package com.cresoty.catpospoint.domain.socket

import kotlinx.coroutines.flow.SharedFlow

interface SocketEventRepository {
    /**
     * 소켓으로 수신된 전문 이벤트 스트림.
     * SharedFlow 이므로 여러 ViewModel 이 동시에 collect 가능하다.
     */
    val events: SharedFlow<SocketEvent>
}
