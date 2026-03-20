package com.cresoty.catpospoint.data.repository.impl

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.repository.UpdateRepository
import com.cresoty.catpospoint.remote.api.CatposCloudApi
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

    // 버전 체크 → 업데이트 필요하면 installUrl emit, 불필요하면 완료
    override fun checkForUpdate(): Flow<DataResource<String>> = flow {
        emit(DataResource.Loading)
        val versionInfo = apiService.getAppVersion(APP_TYPE)
        if (isUpdateRequired(BuildConfig.VERSION_NAME, versionInfo.latestVersion)) {
            emit(DataResource.Success(versionInfo.installUrl))
        }
    }.catch { emit(DataResource.Error(it)) }

    // 다운로드 실행
    override fun downloadUpdate(installUrl: String): Flow<DataResource<Uri>> = callbackFlow {
        trySend(DataResource.Loading)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(installUrl.toUri())
            .setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "catpos_update.apk")
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

    // current < min 이면 업데이트 필요
    private fun isUpdateRequired(current: String, min: String): Boolean {
        Log.d("jhs", "현재 버전:$current")
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        val m = min.split(".").map { it.toIntOrNull() ?: 0 }
        val len = maxOf(c.size, m.size)
        for (i in 0 until len) {
            val cv = c.getOrElse(i) { 0 }
            val mv = m.getOrElse(i) { 0 }
            if (cv < mv) return true
            if (cv > mv) return false
        }
        return false
    }

    companion object {
        private const val APP_TYPE = "catpos"
    }
}
