package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.data.repository.ConfigRepository
import com.cresoty.catpospoint.model.state.ConfigState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetConfigUseCase @Inject constructor(
    private val repository: ConfigRepository
) {
    operator fun invoke(): StateFlow<ConfigState> = repository.configState
}
