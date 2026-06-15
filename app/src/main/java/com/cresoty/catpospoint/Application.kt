package com.cresoty.catpospoint

import android.app.Application
import android.provider.Settings
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.data.repository.ConfigRepository
import com.cresoty.catpospoint.domain.repository.FcmRepository
import com.cresoty.catpospoint.util.CrashlyticsLogger
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class Application : Application() {

    @Inject lateinit var configRepository: ConfigRepository
    @Inject lateinit var fcmRepository: FcmRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        registerStoreIdentity()
    }

    private fun registerStoreIdentity() {
        val androidId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID,
        ) ?: ""

        appScope.launch {
            val fcmToken = fcmRepository.getToken().orEmpty()
            configRepository.configState
                .map { it.bizNo }
                .distinctUntilChanged()
                .collect { taxNo ->
                    CrashlyticsLogger.setStoreIdentity(
                        taxNo = taxNo,
                        androidId = androidId,
                        fcmToken = fcmToken,
                    )
                }
        }
    }
}
