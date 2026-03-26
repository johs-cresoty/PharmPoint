package com.cresoty.catpospoint.presentation.result

enum class ResultStatus {
    FETCH_SUCCESS,     // 조회 완료
    EARN_SUCCESS,      // 적립 완료
    USE_SUCCESS,       // 사용 완료
    USE_UNAVAILABLE    // 사용 불가
}

object ResultContract {
    data class State(
        val status: ResultStatus = ResultStatus.FETCH_SUCCESS,
        val title: String = "",
        val subTitle: String = "",
        val pointTitle: String = "",
        val balancePoint: Int = 0,
        val remainingPoint: Int = 0,
        val timeOut: Int = 5,
    )

    sealed interface Event {
        data class Init(val resultState: State) : Event
        data object GoToWaiting : Event
    }

    sealed interface Effect {
        data object GoToWaiting : Effect
    }
}