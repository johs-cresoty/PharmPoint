package com.cresoty.catpossignpad.remote.di

import com.cresoty.catpossignpad.data.remote.PointRemoteDataSource
import com.cresoty.catpossignpad.remote.impl.PointRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindPointRemoteDataSource(
        source: PointRemoteDataSourceImpl
    ): PointRemoteDataSource
}