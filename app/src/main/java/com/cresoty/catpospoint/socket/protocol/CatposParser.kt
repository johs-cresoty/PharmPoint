package com.cresoty.catpospoint.socket.protocol

import com.cresoty.catpospoint.byte2String

object CatposParser {

    fun parse(data: ByteArray): CatposMessage? {
        val parts = data.byte2String().trim().split("|")

        if (parts.size < 2) return null

        val system = parts[0]
        val command = parts[1]
        val fields = parts.drop(2)

        return CatposMessage(system, command, fields)
    }
}