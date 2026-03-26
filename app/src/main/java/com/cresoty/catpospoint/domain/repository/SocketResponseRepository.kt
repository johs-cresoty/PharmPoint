package com.cresoty.catpospoint.domain.repository

interface SocketResponseRepository {
    fun sendCATPhoneNumber(phoneNumber: String)
    fun sendCATCustomerInfo(customerPhone: String, customerCode: String)
    fun sendCATFail()
    /** TERMINAL 004 : 포인트 사용 응답 전문 전송 */
    fun sendTerminalUsePoint(phone: String, balance: String, delta: String)
    /** CAT 006 : 포인트 사용 결과 응답 전송 */
    fun sendCATUsePointResult(customerCode: String, balance: String, usePoint: String)
    /** CAT 007 : 포인트 사용 결과 응답 전송 (고객 선택 완료) */
    fun sendCATUsePointWithCustomerResult(usePoint: String)
}
