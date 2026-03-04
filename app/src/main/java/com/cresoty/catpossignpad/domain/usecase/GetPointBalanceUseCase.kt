package com.cresoty.catpossignpad.domain.usecase

import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.PointBalanceResult
import com.cresoty.catpossignpad.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPointBalanceUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(
        taxNo: String,
        customerPhone: String
    ): Flow<DataResource<PointBalanceResult>> =
        repository.getPointBalance(taxNo = taxNo, customerPhone = customerPhone)
}
