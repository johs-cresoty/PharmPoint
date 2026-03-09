package com.cresoty.catpospoint

import android.text.TextUtils
import java.io.UnsupportedEncodingException
import kotlin.experimental.or
import kotlin.experimental.xor

object PharmpayTelegram {

    /**
     * 포인트 사용 전문 생성
     *
     * @param phone
     * @param balance
     * @param delta
     * @return
     */
    fun makeUsePoint(
        phone : String,
        balance : String,
        delta : String
    ) : ByteArray {
        val buff = mutableListOf<Byte>()
        var byTmp : MutableList<Byte>

        makeTelegramHeader(buff, Val.TERMINAL_COMMAND_004)
        buff.add(Val.COMM_FS)

        try {
            byTmp = phone.stringToMutableByte()
            buff.addAll(byTmp)
            buff.add(Val.COMM_FS)

            byTmp = balance.stringToMutableByte()
            buff.addAll(byTmp)
            buff.add(Val.COMM_FS)

            byTmp = delta.stringToMutableByte()
            buff.addAll(byTmp)

            buff.add(Val.COMM_ETX)

        } catch ( e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        makeTelegramLRC(buff)

        return buff.toByteArray()
    }

    /**
     * 사용중 취소전문 생성
     *
     * @return
     */
    fun makeInit() : ByteArray {
        val buff = mutableListOf<Byte>()
        val byTmp : MutableList<Byte>

        makeTelegramHeader(buff, Val.TERMINAL_COMMAND_010)
        buff.add(Val.COMM_FS)

        try {
            byTmp = "INIT".stringToMutableByte()
            buff.addAll(byTmp)

            buff.add(Val.COMM_ETX)

        } catch ( e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return buff.toByteArray()
    }

    /**
     * 단말기 통신전문 Header 조합
     *
     * @param data
     * @param cmd
     * @return
     */
    private fun makeTelegramHeader(data: MutableList<Byte>, cmd: String): Int {
        data.add(Val.COMM_STX)
        data.add('X'.code.toByte())
        data.add('X'.code.toByte())

        data.add('P'.code.toByte())
        data.add('A'.code.toByte())
        data.add('D'.code.toByte())
        if (!TextUtils.isEmpty(cmd)) {
            try {
                for (i in cmd.indices) {
                    data.add(cmd[i].code.toByte())
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }

        return data.size
    }

    /**
     * 단말기 통신전문 LRC 계산 및 전문 길이 삽입
     *
     * @param data
     * @param len
     */
    private fun makeTelegramLRC(data: MutableList<Byte>) {
        try {
            val sendLen = String.format("%04d", data.size + 4)
            data.addAll(3, sendLen.stringToMutableByte())

            data.add(getLRC(data.toByteArray(), data.size))

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    /**
     * LRC 계산
     *
     * @param data
     * @param byLength
     * @return
     */
    private fun getLRC(data: ByteArray, byLength: Int): Byte {
        var byLRC: Byte = 0
        for (i in 1 until byLength) {
            byLRC = byLRC.xor(data[i])
        }

        byLRC = byLRC.or(0x20.toByte()) // 0x20은 Int로 취급되므로 Byte로 변환

        return byLRC
    }
}