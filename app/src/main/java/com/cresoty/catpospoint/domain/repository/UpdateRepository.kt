package com.cresoty.catpospoint.domain.repository

import android.net.Uri
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.UpdateInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UpdateRepository {
    fun checkForUpdate(): Flow<DataResource<UpdateInfo>>
    fun downloadUpdate(installUrl: String): Flow<DataResource<Uri>>
    /** 다운로드 진행률 0–100. 다운로드 중이 아니면 null. */
    val downloadProgress: StateFlow<Int?>
}
