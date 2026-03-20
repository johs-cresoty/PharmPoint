package com.cresoty.catpospoint.domain.repository

import android.net.Uri
import com.cresoty.catpospoint.dataresource.DataResource
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    fun checkForUpdate(): Flow<DataResource<String>>          // installUrl emit (업데이트 필요 시)
    fun downloadUpdate(installUrl: String): Flow<DataResource<Uri>>
}
