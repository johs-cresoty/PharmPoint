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
import com.cresoty.catpospoint.remote.api.AppSupportApi
import com.cresoty.catpospoint.remote.model.request.AppVersionCheckRequest
import timber.log.Timber
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class UpdateRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: AppSupportApi,
) : UpdateRepository {

    private val _downloadProgress = MutableStateFlow<Int?>(null)
    override val downloadProgress: StateFlow<Int?> = _downloadProgress.asStateFlow()

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
        _downloadProgress.value = null

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(installUrl.toUri())
            .setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "catpos_update.apk")
        val downloadId = dm.enqueue(request)

        // 300ms 간격으로 진행률 폴링 → _downloadProgress 업데이트
        launch {
            while (isActive) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = dm.query(query)
                if (cursor.moveToFirst()) {
                    val downloaded = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    )
                    val total = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    )
                    if (total > 0) {
                        _downloadProgress.value = (downloaded * 100 / total).toInt()
                    }
                }
                cursor.close()
                delay(300)
            }
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    val apkUri = dm.getUriForDownloadedFile(id)
                    if (apkUri != null) {
                        _downloadProgress.value = 100
                        trySend(DataResource.Success(apkUri))
                    } else {
                        val query = DownloadManager.Query().setFilterById(id)
                        val cursor = dm.query(query)
                        if (cursor.moveToFirst()) {
                            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                            val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                            Timber.e("downloadUpdate 실패: status=$status, reason=$reason")
                        }
                        cursor.close()
                        trySend(DataResource.Error(Exception("다운로드 실패: apkUri null")))
                    }
                    _downloadProgress.value = null
                    close()
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_EXPORTED
        )

        awaitClose {
            _downloadProgress.value = null
            context.unregisterReceiver(receiver)
        }
    }

    companion object {
        private const val PLATFORM = "pharmPoint"
    }
}
