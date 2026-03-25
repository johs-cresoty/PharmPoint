package com.cresoty.catpospoint.presentation.idle


object IdleContract {
    data class State(
        val isPreview: Boolean = false,
    )

    sealed interface Event {
        data object GoToWaiting : Event
    }

    sealed interface Effect {

    }
}