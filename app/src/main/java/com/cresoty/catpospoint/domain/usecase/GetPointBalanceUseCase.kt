package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.PointBalanceResult
import com.cresoty.catpospoint.domain.repository.PointRepository
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
