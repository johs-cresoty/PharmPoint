package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import javax.inject.Inject

class SendTerminalUsePointUseCase @Inject constructor(
    private val repository: SocketResponseRepository,
) {
    operator fun invoke(phone: String, balance: String, delta: String) =
        repository.sendTerminalUsePoint(phone, balance, delta)
}
