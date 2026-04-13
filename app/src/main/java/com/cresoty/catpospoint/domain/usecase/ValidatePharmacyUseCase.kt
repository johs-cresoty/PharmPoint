package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.AppSupportAuthRepository
import javax.inject.Inject

class ValidatePharmacyUseCase @Inject constructor(
    private val authRepository: AppSupportAuthRepository,
) {
    suspend operator fun invoke(bizNo: String): Pair<Boolean, String> = authRepository.validatePharmacy(bizNo)
}
