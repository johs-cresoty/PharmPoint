package com.cresoty.catpospoint.data.fcm.di

import com.cresoty.catpospoint.data.fcm.FcmEventRepositoryImpl
import com.cresoty.catpospoint.domain.fcm.FcmEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FcmModule {

    @Binds
    @Singleton
    abstract fun bindFcmEventRepository(impl: FcmEventRepositoryImpl): FcmEventRepository
}
