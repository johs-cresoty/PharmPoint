package com.cresoty.catpospoint.data.repository.impl

import android.util.Log
import com.cresoty.catpospoint.PharmpayTelegram
import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import com.cresoty.catpospoint.socket.SocketManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketResponseRepositoryImpl @Inject constructor(
    private val socketManager: SocketManager,
) : SocketResponseRepository {

    override fun sendCATPhoneNumber(phoneNumber: String) {
        val bytes = "OK|$phoneNumber\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(bytes) { written ->
            Log.d("SocketDebug", "CAT 전화번호 응답: $written bytes, OK|$phoneNumber")
        }
    }

    override fun sendCATCustomerInfo(customerPhone: String, customerCode: String) {
        val bytes = "OK|$customerPhone|$customerCode\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(bytes) { written ->
            Log.d("SocketDebug", "CAT 고객정보 응답: $written bytes, OK|$customerPhone|$customerCode")
        }
    }

    override fun sendCATFail() {
        val bytes = "FAIL|100|다음에하기\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(bytes)
    }

    override fun sendTerminalUsePoint(phone: String, balance: String, delta: String) {
        val buff = PharmpayTelegram.makeUsePoint(phone, balance, delta)
        socketManager.send(buff) { written ->
            Log.d("SocketDebug", "단말기 포인트사용 응답(004) 전송: $written bytes")
        }
    }

    override fun sendCATUsePointResult(customerCode: String, balance: String, usePoint: String) {
        val bytes = "OK|$customerCode|$balance|$usePoint\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(bytes) { written ->
            Log.d("SocketDebug", "캣포스 포인트사용 응답(006) 전송: $written bytes")
        }
    }

    override fun sendCATUsePointWithCustomerResult(usePoint: String) {
        val bytes = "OK|$usePoint\r\n".toByteArray(Charsets.UTF_8)
        socketManager.send(bytes) { written ->
            Log.d("SocketDebug", "캣포스 포인트사용 응답(007) 전송: $written bytes")
        }
    }
}
