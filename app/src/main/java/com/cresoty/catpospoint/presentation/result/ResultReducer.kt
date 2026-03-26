package com.cresoty.catpospoint.presentation.result


import javax.inject.Inject


class ResultReducer @Inject constructor() {
    fun reduce(
        state: ResultContract.State,
        event: ResultContract.Event
    ): Pair<ResultContract.State, List<ResultContract.Effect>> {
        return when (event) {
            is ResultContract.Event.Init ->
                event.resultState to emptyList()

            ResultContract.Event.GoToWaiting ->
                state to listOf(ResultContract.Effect.GoToWaiting)
        }
    }
}