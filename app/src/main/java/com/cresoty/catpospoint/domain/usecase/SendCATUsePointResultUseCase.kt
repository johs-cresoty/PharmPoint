package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import javax.inject.Inject

class SendCATUsePointResultUseCase @Inject constructor(
    private val repository: SocketResponseRepository,
) {
    operator fun invoke(customerCode: String, balance: String, usePoint: String) =
        repository.sendCATUsePointResult(customerCode, balance, usePoint)
}
