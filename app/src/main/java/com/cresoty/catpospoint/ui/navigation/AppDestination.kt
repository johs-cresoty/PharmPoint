package com.cresoty.catpospoint.ui.navigation

/**
 * NavHost 의 목적지 정의.
 * 문자열 route 를 sealed class 로 감싸 오타와 중복을 방지한다.
 */
sealed class AppDestination(val route: String) {

    /** 대기 화면 (시작 목적지) */
    data object Idle : AppDestination("idle")

//    /** 포인트 적립 화면 */
//    data object EarnPoint : AppDestination("earn_point")

    /** 휴대폰 번호 입력 화면 (고객 조회 / 적립) */
    data object PhoneNumberInput : AppDestination("phone_number_input")

    /** 포인트 사용 화면 */
    data object UsePoint : AppDestination("use_point")

    /** 처리 결과 화면 */
    data object Result : AppDestination("result")

    /** CATPOS 휴대폰 번호 요청 화면 (CAT NUM / CAT CST 공용) */
    data object CatRequestCustomer : AppDestination("cat_request_customer")
}
