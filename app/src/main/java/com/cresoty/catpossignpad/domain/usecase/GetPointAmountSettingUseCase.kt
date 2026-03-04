package com.cresoty.catpossignpad.domain.usecase

import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.PointAmountSettingResult
import com.cresoty.catpossignpad.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPointAmountSettingUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(taxNo: String): Flow<DataResource<PointAmountSettingResult>> =
        repository.getPointAmountSetting(taxNo)
}
