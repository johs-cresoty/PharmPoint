package com.cresoty.catpospoint.presentation.idle

import android.net.Uri
import com.cresoty.catpospoint.model.enums.PaymentType
import com.cresoty.catpospoint.model.enums.PointUseSource
import com.cresoty.catpospoint.model.state.ConfigState


object IdleContract {
    data class State(
        val isPreview: Boolean = false,
        /** 프리뷰 테마 인덱스 (null이면 현재 설정 테마 사용) */
        val preTheme: Int? = null,
        /** 프리뷰 서브타이틀 (null이면 현재 설정 서브타이틀 사용) */
        val preSubTitle: String? = null,
        /** 프리뷰 커스텀 이미지 URI */
        val preCustomImageUri: Uri? = null,
        /** 현재 설정 config (IdleScreen에서 storeName/subTitle 등 표시용) */
        val config: ConfigState = ConfigState(),
    )

    sealed interface Event {
        data object Init : Event
        data object OnTripleTap : Event
        data object OnClickPointBalance : Event
        /** 프리뷰 닫기 — 순수 상태 리셋, Effect 없음 */
        data object OnClickClose : Event
        /** 설정 다이얼로그에서 프리뷰 열기 요청 */
        data class ShowPreview(
            val theme: Int?,
            val subTitle: String?,
            val customImageUri: Uri?,
        ) : Event
    }

    sealed interface Effect {
        /** bizNo 유무를 체크한 뒤 Route에서 적절한 다이얼로그를 열어야 할 때 발행 */
        data class ShowSettingDialog(val requiresPassword: Boolean) : Effect
        data object NavigateToPointBalance : Effect
    }
}