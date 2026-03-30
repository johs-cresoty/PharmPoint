package com.cresoty.catpospoint.presentation.idle

import com.cresoty.catpospoint.presentation.idle.IdleContract.Effect
import javax.inject.Inject


class IdleReducer @Inject constructor() {
    fun reduce(
        state: IdleContract.State,
        event: IdleContract.Event
    ): Pair<IdleContract.State, List<IdleContract.Effect>> {
        return when (event) {
            IdleContract.Event.Init -> state to emptyList()
            // 비즈니스 로직(bizNo 체크)은 ViewModel에서 처리 — Reducer는 순수 상태 변환만 담당
            IdleContract.Event.OnTripleTap -> state to emptyList()
            IdleContract.Event.OnClickPointBalance -> state to listOf(Effect.NavigateToPointBalance)
            // 프리뷰 닫기: 순수 상태 리셋, Effect 없음 (콜백 불필요)
            IdleContract.Event.OnClickClose -> state.copy(
                isPreview = false,
                preTheme = null,
                preSubTitle = null,
                preCustomImageUri = null,
            ) to emptyList()
            // 프리뷰 열기: 순수 상태 세팅, Effect 없음
            is IdleContract.Event.ShowPreview -> state.copy(
                isPreview = true,
                preTheme = event.theme,
                preSubTitle = event.subTitle,
                preCustomImageUri = event.customImageUri,
            ) to emptyList()
        }
    }
}