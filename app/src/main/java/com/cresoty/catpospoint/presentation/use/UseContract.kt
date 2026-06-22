package com.cresoty.catpospoint.presentation.use

import com.cresoty.catpospoint.model.enums.PointQuickInputType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.presentation.result.ResultContract


object UseContract {
    data class State(
        val source: PointUseSource = PointUseSource.TERMINAL,
        val phoneNumber: String = "",    // TERMINAL 응답 전문용
        val customerCode: String = "",   // CAT 응답 전문용
        val customerName: String = "",   // 고객 이름 (없을 수 있음)
        val storeName: String = "",
        val payAmount: Int = 0,
        val usePoint: Int = 0,
        val usePointInput: String = "",  // 숫자패드 입력 버퍼
        val balancePoint: Int = 0,
        val minPoint: Int = 0,
        val isMinPointEnabled: Boolean = false
    )

    sealed interface Event {
        data class Init(val useState: State) : Event
        data class OnNumberInput(val digit: String) : Event
        data object OnDeleteOne : Event
        data object OnDeleteAll : Event
        data class OnQuickInput(val type: PointQuickInputType) : Event

        data class OnClickConfirm(val usePoint: Int) : Event
        data object OnClickClose : Event
        data object OnClickBack : Event
    }

    sealed interface Effect {
        data object GoToBack : Effect
        data object GoToIdle : Effect
        data object SendCATFailAndGoToIdle : Effect
        data class SubmitUsePoint(val usePoint: Int) : Effect         // ViewModel 내부 처리용
        data class GoToResultScreen(val resultState: ResultContract.State) : Effect  // Route 전달용
    }
}