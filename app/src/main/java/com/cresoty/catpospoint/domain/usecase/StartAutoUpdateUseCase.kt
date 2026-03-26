package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.UpdateInfo
import com.cresoty.catpospoint.domain.repository.UpdateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartAutoUpdateUseCase @Inject constructor(
    private val repo: UpdateRepository
) {
    operator fun invoke(): Flow<DataResource<UpdateInfo>> = repo.checkForUpdate()
}
