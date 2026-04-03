package com.cresoty.catpospoint.domain.usecase

import android.net.Uri
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.repository.UpdateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class StartDownloadUseCase @Inject constructor(
    private val repo: UpdateRepository
) {
    operator fun invoke(installUrl: String): Flow<DataResource<Uri>> = repo.downloadUpdate(installUrl)
    val progress: StateFlow<Int?> get() = repo.downloadProgress
}
