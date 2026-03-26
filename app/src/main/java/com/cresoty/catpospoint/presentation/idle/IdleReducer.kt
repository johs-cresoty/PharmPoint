package com.cresoty.catpospoint.presentation.idle

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
            IdleContract.Event.OnClickPointBalance -> state to listOf(IdleContract.Effect.NavigateToPointBalance)
        }
    }
}