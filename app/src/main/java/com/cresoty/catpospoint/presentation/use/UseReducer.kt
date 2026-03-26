package com.cresoty.catpospoint.presentation.use

import com.cresoty.catpospoint.model.enums.PointQuickInputType
import com.cresoty.catpospoint.model.enums.PointUseSource
import javax.inject.Inject


class UseReducer @Inject constructor() {
    fun reduce(
        state: UseContract.State,
        event: UseContract.Event
    ): Pair<UseContract.State, List<UseContract.Effect>> {
        return when (event) {

            is UseContract.Event.Init ->
                event.useState to emptyList()

            is UseContract.Event.OnNumberInput -> {
                val newInput = if (state.usePointInput.length < 7)
                    state.usePointInput + event.digit
                else state.usePointInput
                val maxPoint = minOf(state.balancePoint, state.payAmount)
                val raw = newInput.toIntOrNull() ?: 0
                val newPoint = minOf(raw, maxPoint)
                // 초과 시 입력 버퍼도 실제 값으로 정정
                val finalInput = if (newPoint < raw) newPoint.toString() else newInput
                state.copy(usePointInput = finalInput, usePoint = newPoint) to emptyList()
            }

            UseContract.Event.OnDeleteOne -> {
                val newInput = state.usePointInput.dropLast(1)
                state.copy(
                    usePointInput = newInput,
                    usePoint = newInput.toIntOrNull() ?: 0,
                ) to emptyList()
            }

            UseContract.Event.OnDeleteAll ->
                state.copy(usePointInput = "", usePoint = 0) to emptyList()

            is UseContract.Event.OnQuickInput -> {
                val maxPoint = minOf(state.balancePoint, state.payAmount)
                val newPoint = when (event.type) {
                    PointQuickInputType.PLUS_100 -> minOf(state.usePoint + 100, maxPoint)
                    PointQuickInputType.PLUS_1000 -> minOf(state.usePoint + 1000, maxPoint)
                    PointQuickInputType.PLUS_ALL -> maxPoint
                }
                val newInput = if (newPoint > 0) newPoint.toString() else ""
                state.copy(usePointInput = newInput, usePoint = newPoint) to emptyList()
            }

            is UseContract.Event.OnClickConfirm ->
                state to listOf(UseContract.Effect.SubmitUsePoint(event.usePoint))

            UseContract.Event.OnClickBack -> state to listOf(UseContract.Effect.GoToBack)
            UseContract.Event.OnClickClose ->
                if (state.source == PointUseSource.CAT)
                    state to listOf(UseContract.Effect.SendCATFailAndGoToIdle)
                else
                    state to listOf(UseContract.Effect.GoToIdle)

        }
    }
}
