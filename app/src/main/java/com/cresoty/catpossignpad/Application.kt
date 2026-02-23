package com.cresoty.catpossignpad

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.cresoty.catpossignpad.network.NetworkManager
import com.cresoty.catpossignpad.socket.SocketManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class Application : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}

class AppContainer(appContext: Context) {

    private val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile("app_config") }
        )

    private val appScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val configRepository: ConfigRepository =
        ConfigRepository(
            dataStore = dataStore,
            appScope = appScope
        )

    val networkManager = NetworkManager(
        configState = configRepository.configState,
        appScope = appScope
    )

    val socketManager = SocketManager()
}