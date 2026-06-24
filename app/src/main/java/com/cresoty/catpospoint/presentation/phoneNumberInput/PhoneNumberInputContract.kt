package com.cresoty.catpospoint.presentation.phoneNumberInput

import com.cresoty.catpospoint.domain.model.TransactionData
import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.result.ResultContract
import com.cresoty.catpospoint.presentation.use.UseContract


object PhoneNumberInputContract {
    sealed class Mode {
        data class Save(
            val source: PointUseSource,
            val paymentType: PaymentType = PaymentType.SINGLE,
        ) : Mode()  // 포인트 적립

        data class Lookup(val source: PointUseSource) : Mode()  // 포인트 사용 (고객 조회)
        data object CatRequestNum : Mode()       // CAT 001: 휴대폰 번호 요청
        data object CatRequestCustomer : Mode()  // CAT 002: 휴대폰 번호 + 고객 번호 요청
    }

    data class State(
        val mode: Mode = Mode.Save(PointUseSource.TERMINAL),
        val phoneNumber: String = "010",
        val bizNo: String = "",
        val storeName: String = "",
        val payAmount: Int = 0,
        val estimatedPoint: Int = 0,
        val customerName: String = "",
        val balancePoint: Int = 0,
        val customerCode: String = "",
        val isCustomerExist: Boolean? = null,  // null=미체크 또는 에러, true=존재, false=없음
        val isMasked: Boolean = true,
        val isCheckBox: Boolean = true,
        val isLoading: Boolean = false,
        // 서버/네트워크 에러 시 사용자에게 보여줄 메시지. null 이면 다이얼로그 숨김
        val errorMessage: String? = null,
        // 포인트 적립 API 호출용 거래 데이터 (Mode.Save 에서만 사용)
        val trnDate: String = "",
        val trnTime: String = "",
        val trnGubn: String = "",
        val appNum: String = "",
        val sleSeq: String = "",
    )

    sealed interface Event {
        /** 화면 진입 시 모드(적립/조회)와 파싱된 거래 데이터를 전달 */
        data class Init(val mode: Mode, val transactionData: TransactionData?) : Event
        data class OnInitSuccess(val bizNo: String, val storeName: String) : Event
        data class OnEstimatePointSuccess(val estimatedPoint: Int, val sleSeq: String) : Event
        data class OnSavePointSuccess(val resultState: ResultContract.State) : Event
        data object OnSavePointError : Event
        data class OnClickConfirm(
            val phoneNumber: String,
            val resultState: ResultContract.State? = null,
        ) : Event
        data object OnClickClose : Event
        data object GoToTheWaitingScreen : Event
        data class OnClickCheckBox(val isCheck: Boolean) : Event
        data class OnNumberInput(val digit: String) : Event
        data object OnDeleteOne : Event
        data object OnDeleteAll : Event
        data object OnMaskToggle : Event
        data class OnCustomerCheckResult(val exists: Boolean, val balancePoint: Int, val customerCode: String, val customerName: String) : Event
        /** API 에러 발생 시 사용자 안내용 메시지 표시 */
        data class OnApiError(val message: String) : Event
        /** 에러 다이얼로그 확인 버튼 — 다이얼로그 닫기 */
        data object OnDismissErrorDialog : Event
        data object Loading : Event
    }

    sealed interface Effect {
        data object LoadData : Effect
        data object RequestSavePoint : Effect
        data class SendToCATCustomerInfo(val bizNo: String, val phoneNumber: String) : Effect
        data class SendToCATPhoneNumber(val phoneNumber: String) : Effect
        data object SendCATFail : Effect
        data object GoToTheWaitingScreen : Effect
        data class GoToUsePointScreen(val usePointState: UseContract.State) : Effect
        data class GoToTheResultScreen(val resultState: ResultContract.State) : Effect
        data class CheckCustomerExist(val phoneNumber: String) : Effect
        data object RequestNavigateToUsePoint : Effect
        data object ProceedAfterCustomerCheck : Effect
    }
}
