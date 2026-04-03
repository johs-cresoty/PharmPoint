package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.DeviceRepository
import javax.inject.Inject

class RegisterDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
) {
    suspend operator fun invoke() = deviceRepository.registerDevice()
}
