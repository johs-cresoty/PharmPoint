package com.cresoty.catpospoint.data.service

import com.cresoty.catpospoint.domain.fcm.FcmEvent
import com.cresoty.catpospoint.domain.fcm.FcmEventRepository
import com.cresoty.catpospoint.domain.usecase.RegisterDeviceUseCase
import com.cresoty.catpospoint.domain.usecase.SaveFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject lateinit var saveFcmTokenUseCase: SaveFcmTokenUseCase
    @Inject lateinit var registerDeviceUseCase: RegisterDeviceUseCase
    @Inject lateinit var fcmEventRepository: FcmEventRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        Log.d("FCM_TOKEN", "토큰 갱신: $token")
        serviceScope.launch {
            saveFcmTokenUseCase(token)
            registerDeviceUseCase()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: ""
        val body = message.notification?.body ?: ""
        Log.d("FCM_MESSAGE", "data=${message.data}")
        serviceScope.launch {
            fcmEventRepository.emit(FcmEvent.MessageReceived(title, body, message.data))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
