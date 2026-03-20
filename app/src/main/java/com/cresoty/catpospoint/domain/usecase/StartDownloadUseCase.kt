package com.cresoty.catpospoint.domain.usecase

import android.net.Uri
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.repository.UpdateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartDownloadUseCase @Inject constructor(
    private val repo: UpdateRepository
) {
    operator fun invoke(installUrl: String): Flow<DataResource<Uri>> = repo.downloadUpdate(installUrl)
}
