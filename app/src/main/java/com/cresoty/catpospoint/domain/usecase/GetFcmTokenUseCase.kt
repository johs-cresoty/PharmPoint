package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.FcmRepository
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(
    private val fcmRepository: FcmRepository,
) {
    suspend operator fun invoke(): String? = fcmRepository.getToken()
}
