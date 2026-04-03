package com.cresoty.catpospoint.data.repository.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkRequest
import android.provider.Settings
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.data.repository.ConfigKey
import com.cresoty.catpospoint.data.repository.ConfigRepository
import com.cresoty.catpospoint.domain.repository.DeviceRepository
import com.cresoty.catpospoint.domain.repository.FcmRepository
import com.cresoty.catpospoint.remote.api.AppSupportApi
import com.cresoty.catpospoint.remote.model.request.RegisterDeviceRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.inject.Inject

internal class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: AppSupportApi,
    private val fcmRepository: FcmRepository,
    private val configRepository: ConfigRepository,
) : DeviceRepository {

    override suspend fun registerDevice() {
        val fcmToken = fcmRepository.getToken() ?: return

        val bizNo = configRepository.getValue(ConfigKey.BIZ_NO, "")
        val request = RegisterDeviceRequest(
            androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
            ),
            businessRegistrationNumber = bizNo.replace("-", ""),
            currentVersionCode = BuildConfig.VERSION_CODE,
            fcmToken = fcmToken,
            ip = getLocalIpAddress(),
            platform = PLATFORM,
        )

        runCatching { api.registerDevice(request) }
            .onFailure { Timber.e(it, "registerDevice 실패") }
    }

    override fun observeIpChanges(): Flow<Unit> = callbackFlow {
        var lastIp = getLocalIpAddress()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // onLinkPropertiesChanged: IP가 실제로 할당된 시점에 호출됨
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                val current = linkProperties.linkAddresses
                    .map { it.address }
                    .filterIsInstance<Inet4Address>()
                    .firstOrNull { !it.isLoopbackAddress }
                    ?.hostAddress ?: return   // IP 아직 없으면 무시
                if (current != lastIp) {
                    lastIp = current
                    trySend(Unit)
                }
            }
        }

        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback,
        )

        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    private fun getLocalIpAddress(): String = try {
        NetworkInterface.getNetworkInterfaces()
            .toList()
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress ?: ""
    } catch (e: Exception) { "" }

    companion object {
        private const val PLATFORM = "pharmPoint"
    }
}
