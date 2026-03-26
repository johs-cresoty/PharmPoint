package com.cresoty.catpospoint.domain.socket

import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource

/**
 * 소켓으로 수신된 전문을 파싱한 도메인 이벤트.
 * SocketEventRepositoryImpl 에서 생성되며, 여러 ViewModel 이 SharedFlow 로 동시 수신한다.
 */
sealed class SocketEvent {

    // ── 단말기(TRM) 전문 ────────────────────────────────────────

    /** 001 : 포인트 적립 요청 (단일 결제) */
    data class TerminalEarnPointSingle(val data: List<String>) : SocketEvent()

    /** 002 : 포인트 적립 요청 (복합 결제) */
    data class TerminalEarnPointComplex(val data: List<String>) : SocketEvent()

    /** 003 : 포인트 사용 요청 */
    data class TerminalUsePoint(val data: List<String>) : SocketEvent()

    // ── 캣포스(CAT) 전문 ────────────────────────────────────────

    /** 000 : 연결 테스트 */
    data object CatConnect : SocketEvent()

    /** 001 : 휴대폰 번호 요청 */
    data object CatRequestNum : SocketEvent()

    /** 002 : 휴대폰 번호 + 고객 번호 요청 */
    data object CatRequestCustomer : SocketEvent()

    /** 003 : 연결 해제 */
    data object CatDisconnect : SocketEvent()

    /** 004 : 포인트 적립 (단일 결제) */
    data class CatEarnPointSingle(val fields: List<String>) : SocketEvent()

    /** 005 : 포인트 적립 (복합 결제) */
    data class CatEarnPointComplex(val fields: List<String>) : SocketEvent()

    /** 006 : 포인트 사용 (고객 미선택) */
    data class CatUsePointNoCustomer(val fields: List<String>) : SocketEvent()

    /** 007 : 포인트 사용 (고객 선택) */
    data class CatUsePointWithCustomer(val fields: List<String>) : SocketEvent()
}
