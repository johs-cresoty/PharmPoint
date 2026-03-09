package com.cresoty.catpospoint.remote.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.cresoty.catpospoint.ConfigRepository
import com.cresoty.catpospoint.remote.api.CatposCloudApi
import com.cresoty.catpospoint.remote.api.createApiService
import com.cresoty.catpospoint.socket.SocketManager
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
    fun provideSocketManager(): SocketManager = SocketManager()
}