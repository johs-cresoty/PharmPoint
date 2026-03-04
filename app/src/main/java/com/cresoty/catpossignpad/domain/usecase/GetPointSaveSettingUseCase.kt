package com.cresoty.catpossignpad.domain.usecase

import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.PointSaveSettingResult
import com.cresoty.catpossignpad.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPointSaveSettingUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(taxNo: String): Flow<DataResource<PointSaveSettingResult>> =
        repository.getPointSaveSetting(taxNo)
}
