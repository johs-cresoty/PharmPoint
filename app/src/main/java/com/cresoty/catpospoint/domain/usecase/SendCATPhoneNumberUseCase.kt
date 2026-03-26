package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import javax.inject.Inject

class SendCATPhoneNumberUseCase @Inject constructor(
    private val repository: SocketResponseRepository,
) {
    operator fun invoke(phoneNumber: String) = repository.sendCATPhoneNumber(phoneNumber)
}
