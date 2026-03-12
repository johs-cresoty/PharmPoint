package com.cresoty.catpospoint

import java.nio.charset.Charset

object Val {

    const val TERMINAL_COMMAND_001 = "001"      // TRM -> PAD : 포인트 적립
    const val TERMINAL_COMMAND_002 = "002"      // TRM -> PAD : 포인트 적립(복합결제)
    const val TERMINAL_COMMAND_003 = "003"      // TRM -> PAD : [요청]포인트 사용
    const val TERMINAL_COMMAND_004 = "004"      // TRM <- PAD : [응답]포인트 사용
    const val TERMINAL_COMMAND_010 = "010"      // PAD -> TRM : 취소

    const val CATPOS = "CAT" // CATPOS -> PAD
    const val CATPOS_NUM = "001"
    const val CATPOS_CST = "002"
    const val CATPOS_DISCONNECT = "003"


    const val TERMINAL_FLAG     = "TRM"
    const val COMM_STX : Byte = 0x02
    const val COMM_ETX : Byte = 0x03
    const val COMM_FS : Byte = 0x1C
    const val COMM_ACK : Byte = 0x06
    const val COMM_NAK : Byte = 0x15

    fun getKorCharset(): Charset {
        return Charset.forName("EUC-KR")
    }
 }
