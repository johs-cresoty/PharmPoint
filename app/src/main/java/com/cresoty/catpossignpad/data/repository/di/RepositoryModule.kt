package com.cresoty.catpossignpad.data.repository.di

import com.cresoty.catpossignpad.data.repository.impl.PointRepositoryImpl
import com.cresoty.catpossignpad.domain.repository.PointRepository
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