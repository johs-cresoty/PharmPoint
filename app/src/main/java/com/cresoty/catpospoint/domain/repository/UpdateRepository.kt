package com.cresoty.catpospoint.domain.repository

import android.net.Uri
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.UpdateInfo
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    fun checkForUpdate(): Flow<DataResource<UpdateInfo>>
    fun downloadUpdate(installUrl: String): Flow<DataResource<Uri>>
}
