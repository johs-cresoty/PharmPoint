package com.cresoty.catpospoint.data.repository.di

import com.cresoty.catpospoint.data.repository.impl.PointRepositoryImpl
import com.cresoty.catpospoint.domain.repository.PointRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPointRepository(repo: PointRepositoryImpl): PointRepository
}