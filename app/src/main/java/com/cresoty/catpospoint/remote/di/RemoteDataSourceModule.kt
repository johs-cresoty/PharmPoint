package com.cresoty.catpospoint.remote.di

import com.cresoty.catpospoint.data.remote.PointRemoteDataSource
import com.cresoty.catpospoint.remote.impl.PointRemoteDataSourceImpl
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