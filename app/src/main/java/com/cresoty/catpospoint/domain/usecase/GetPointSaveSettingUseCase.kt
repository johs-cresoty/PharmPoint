package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.PointSaveSettingResult
import com.cresoty.catpospoint.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPointSaveSettingUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(taxNo: String): Flow<DataResource<PointSaveSettingResult>> =
        repository.getPointSaveSetting(taxNo)
}
