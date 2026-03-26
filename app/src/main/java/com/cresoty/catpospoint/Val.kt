package com.cresoty.catpospoint

import java.nio.charset.Charset

object Val {

    const val TERMINAL_COMMAND_001 = "001"      // TRM -> PAD : 포인트 적립
    const val TERMINAL_COMMAND_002 = "002"      // TRM -> PAD : 포인트 적립(복합결제)
    const val TERMINAL_COMMAND_003 = "003"      // TRM -> PAD : [요청]포인트 사용
    const val TERMINAL_COMMAND_004 = "004"      // TRM <- PAD : [응답]포인트 사용
    const val TERMINAL_COMMAND_010 = "010"      // PAD -> TRM : 취소

    const val CATPOS = "CAT" // CATPOS -> PAD
     const val CATPOS_CONNECT = "000"                   // CATPOS -> PAD : 연결 테스트
    const val CATPOS_NUM = "001"
    const val CATPOS_CST = "002"
    const val CATPOS_DISCONNECT = "003"
    const val CATPOS_EARN_POINT = "004"                 // CATPOS -> PAD : 포인트 적립(단일 결제)
    const val CATPOS_EARN_POINT_COMPLEX = "005"         // CATPOS -> PAD : 포인트 적립(복합 결제)
    const val CATPOS_USE_POINT_NO_CUSTOMER = "006"      // CATPOS -> PAD : 포인트 사용(고객 미선택)
    const val CATPOS_USE_POINT_WITH_CUSTOMER = "007"    // CATPOS -> PAD : 포인트 사용(고객 선택)
    const val CATPOS_SESSION_START = "777"              // CATPOS -> PAD : CAT 세션 시작 (단말기 신호 차단)
    const val CATPOS_SESSION_END = "444"                // CATPOS -> PAD : CAT 세션 종료 (단말기 신호 차단 해제)


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
