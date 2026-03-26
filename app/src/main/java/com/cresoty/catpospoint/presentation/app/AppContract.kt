package com.cresoty.catpospoint.presentation.app

import com.cresoty.catpospoint.domain.model.TransactionData
import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.use.UseContract

/**
 * 앱 전역 NavGraph 수준의 MVI 계약.
 *
 * - [State] : 각 화면 진입에 필요한 파라미터를 보관. NavHost composable 이 읽어 Route 에 전달.
 * - [Effect] : 화면 전환 명령. AppNavGraph 의 LaunchedEffect 에서 navController 를 호출.
 */
object AppContract {

    // ── State (네비게이션 파라미터 보관) ─────────────────────────────

    data class State(
        val earnPointArgs: EarnPointArgs? = null,
        val phoneNumberInputArgs: PhoneNumberInputArgs? = null,
        val usePointArgs: UsePointArgs? = null,
        val usePointScreenArgs: UseContract.State? = null,
        val resultArgs: ResultContract.State? = null,
    )

    /** 포인트 적립 화면 진입 파라미터 */
    data class EarnPointArgs(
        val source: PointUseSource,
        val paymentType: PaymentType,
        val transactionData: TransactionData,
    )

    /** 휴대폰 번호 입력 화면 진입 파라미터 */
    data class PhoneNumberInputArgs(
        val mode: PhoneNumberInputContract.Mode,
        val transactionData: TransactionData? = null,  // Mode.Save 에서만 유의미
    )

    /** 포인트 사용 화면 진입 파라미터 */
    data class UsePointArgs(
        val source: PointUseSource,
        val data: List<String>,
    )

    // ── Effect (화면 전환 명령) ──────────────────────────────────────

    sealed interface Effect {
        /** Idle 화면으로 복귀 (백스택 팝) */
        data object NavigateToIdle : Effect

//        /** 포인트 적립 화면 */
//        data object NavigateToEarnPoint : Effect

        /** 휴대폰 번호 입력 화면 */
        data object NavigateToPhoneNumberInput : Effect

        /** 포인트 사용 화면 */
        data object NavigateToUsePoint : Effect

        /** CATPOS 휴대폰 번호 요청 화면 (CAT NUM) */
        data object NavigateToCatRequestNum : Effect

        /** CATPOS 휴대폰 번호 + 고객 번호 요청 화면 (CAT CST) */
        data object NavigateToCatRequestCustomer : Effect
    }
}
