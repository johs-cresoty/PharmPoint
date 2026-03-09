package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.PointAmountSettingResult
import com.cresoty.catpospoint.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPointAmountSettingUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(taxNo: String): Flow<DataResource<PointAmountSettingResult>> =
        repository.getPointAmountSetting(taxNo)
}
