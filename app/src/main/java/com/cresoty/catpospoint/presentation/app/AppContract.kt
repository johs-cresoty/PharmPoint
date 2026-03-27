package com.cresoty.catpospoint.presentation.app

import android.net.Uri
import com.cresoty.catpospoint.domain.model.TransactionData
import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.model.interfaces.Dialogs
import com.cresoty.catpospoint.presentation.phoneNumberInput.PhoneNumberInputContract
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.use.UseContract

/**
 * 앱 전역 NavGraph 수준의 MVI 계약.
 *
 * - [State] : 각 화면 진입에 필요한 파라미터를 보관. NavHost composable 이 읽어 Route 에 전달.
 * - [Event] : ViewModel 로 전달되는 사용자 액션.
 * - [Effect] : 일회성 사이드이펙트. AppNavGraph 의 LaunchedEffect 에서 처리.
 */
object AppContract {

    // ── State ────────────────────────────────────────────────────────

    data class State(
        val earnPointArgs: EarnPointArgs? = null,
        val phoneNumberInputArgs: PhoneNumberInputArgs? = null,
        val usePointArgs: UsePointArgs? = null,
        val usePointScreenArgs: UseContract.State? = null,
        val resultArgs: ResultContract.State? = null,
        /** 업데이트 관련 다이얼로그 (None / UpdateRequired / UpdateBlocked) */
        val dialog: Dialogs = Dialogs.None,
    )

    // ── Event ────────────────────────────────────────────────────────

    sealed interface Event {
        /** 업데이트 차단 다이얼로그에서 "업데이트하기" 버튼 클릭 */
        data object OnAcceptUpdate : Event
    }

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

    // ── Effect (일회성 사이드이펙트) ─────────────────────────────────

    sealed interface Effect {
        /** Idle 화면으로 복귀 (백스택 팝) */
        data object NavigateToIdle : Effect

//        /** 포인트 적립 화면 */
//        data object NavigateToEarnPoint : Effect

        /** 휴대폰 번호 입력 화면 */
        data object NavigateToPhoneNumberInput : Effect

        /** 포인트 사용 화면 */
        data object NavigateToUsePoint : Effect

        /** CATPOS 휴대폰 번호 요청 화면 (CAT NUM / CAT CST 공용) */
        data object NavigateToCatRequestCustomer : Effect

        /** 다운로드 완료 → APK 설치 인텐트 실행 */
        data class InstallApk(val uri: Uri) : Effect

        /** 업데이트 불필요 & 사업자번호 미설정 → 설정 다이얼로그 표출 */
        data object ShowSettingDialog : Effect
    }
}
