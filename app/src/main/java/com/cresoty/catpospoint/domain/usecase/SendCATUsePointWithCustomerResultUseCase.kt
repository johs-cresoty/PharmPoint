package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import javax.inject.Inject

class SendCATUsePointWithCustomerResultUseCase @Inject constructor(
    private val repository: SocketResponseRepository,
) {
    operator fun invoke(usePoint: String) = repository.sendCATUsePointWithCustomerResult(usePoint)
}
