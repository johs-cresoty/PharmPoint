package com.cresoty.catpospoint.socket.protocol

import android.util.Log
import com.cresoty.catpospoint.byte2String

object CatposParser {

    fun parse(data: ByteArray): CatposMessage? {
        Log.d("SocketManager", "캣포스 데이터 원문:${data.byte2String()}")
        val parts = data.byte2String().trim().split("|")

        if (parts.size < 2) return null

        val system = parts[0]
        val command = parts[1]
        val fields = parts.drop(2)

        return CatposMessage(system, command, fields)
    }
}