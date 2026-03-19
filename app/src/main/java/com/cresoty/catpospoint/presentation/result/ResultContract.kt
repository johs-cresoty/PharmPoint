package com.cresoty.catpospoint.presentation.result

object ResultContract {
    data class State(
        val title: String = "",
        val subTitle: String = "",
        val pointTitle: String = "",
        val earnPoint: Int = 0,
        val balancePoint: Int = 0,
        val minRequiredPoint: Int = 0,
        val timeOut: Int = 0,
    )

    sealed interface Event {
        data object GoToWaiting : Event
    }

    sealed interface Effect {

    }
}