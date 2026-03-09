package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.EstimatePointResult
import com.cresoty.catpospoint.domain.model.command.EstimatePointCommand
import com.cresoty.catpospoint.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EstimatePointUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(
        command: EstimatePointCommand
    ): Flow<DataResource<EstimatePointResult?>> = repository.estimatePoint(command)
}
