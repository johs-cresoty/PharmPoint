package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import javax.inject.Inject

class SendCATFailUseCase @Inject constructor(
    private val repository: SocketResponseRepository,
) {
    operator fun invoke() = repository.sendCATFail()
}
