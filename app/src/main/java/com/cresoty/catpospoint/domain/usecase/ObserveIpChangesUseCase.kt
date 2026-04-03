package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIpChangesUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
) {
    operator fun invoke(): Flow<Unit> = deviceRepository.observeIpChanges()
}
