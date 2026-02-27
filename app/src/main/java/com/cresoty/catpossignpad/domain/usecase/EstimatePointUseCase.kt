package com.cresoty.catpossignpad.domain.usecase

import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.EstimatePointResult
import com.cresoty.catpossignpad.domain.model.command.EstimatePointCommand
import com.cresoty.catpossignpad.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EstimatePointUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(
        command: EstimatePointCommand
    ): Flow<DataResource<EstimatePointResult?>> = repository.estimatePoint(command)
}
