package com.cresoty.catpossignpad.remote.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpossignpad.ConfigRepository
import com.cresoty.catpossignpad.remote.api.CatposCloudApi
import com.cresoty.catpossignpad.network.NetworkManager
import com.cresoty.catpossignpad.remote.api.createApiService
import com.cresoty.catpossignpad.socket.SocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGoodsApiService(): CatposCloudApi = createApiService()

    @Provides
    @Singleton
    fun provideAppScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideConfigRepository(
        dataStore: DataStore<Preferences>,
        appScope: CoroutineScope
    ): ConfigRepository = ConfigRepository(
        dataStore = dataStore,
        appScope = appScope
    )

    @Provides
    @Singleton
    fun provideNetworkManager(
        configRepository: ConfigRepository,
        appScope: CoroutineScope
    ): NetworkManager = NetworkManager(
        configState = configRepository.configState,
        appScope = appScope
    )

    @Provides
    @Singleton
    fun provideSocketManager(): SocketManager = SocketManager()
}