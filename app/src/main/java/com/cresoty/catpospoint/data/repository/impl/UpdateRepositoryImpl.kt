package com.cresoty.catpospoint.data.repository.impl

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.UpdateInfo
import com.cresoty.catpospoint.domain.repository.UpdateRepository
import com.cresoty.catpospoint.remote.api.CatposCloudApi
import com.cresoty.catpospoint.remote.model.request.AppVersionCheckRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class UpdateRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: CatposCloudApi
) : UpdateRepository {

    override fun checkForUpdate(): Flow<DataResource<UpdateInfo>> = flow {
        emit(DataResource.Loading)
        val request = AppVersionCheckRequest(
            currentVersionCode = BuildConfig.VERSION_CODE,
            platform = PLATFORM
        )
        val response = apiService.checkAppVersion(request)
        if (response.forceUpdate) {
            emit(DataResource.Success(UpdateInfo(
                installUrl   = response.installUrl,
                messageTitle = response.messageTitle ?: "",
                message      = response.message ?: "",
            )))
        }
    }.catch { emit(DataResource.Error(it)) }

    // 다운로드 실행
    override fun downloadUpdate(installUrl: String): Flow<DataResource<Uri>> = callbackFlow {
        trySend(DataResource.Loading)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(installUrl.toUri())
            .setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "catpos_update.apk")
        val downloadId = dm.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    val apkUri = dm.getUriForDownloadedFile(id)
                    trySend(DataResource.Success(apkUri))
                    close()
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_EXPORTED
        )

        awaitClose { context.unregisterReceiver(receiver) }
    }

    companion object {
        private const val PLATFORM = "PharmPoint"
    }
}
