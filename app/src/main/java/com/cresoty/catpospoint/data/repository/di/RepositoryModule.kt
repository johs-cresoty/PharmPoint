package com.cresoty.catpospoint.data.repository.di

import com.cresoty.catpospoint.data.repository.impl.PointRepositoryImpl
import com.cresoty.catpospoint.data.repository.impl.SocketResponseRepositoryImpl
import com.cresoty.catpospoint.data.repository.impl.UpdateRepositoryImpl
import com.cresoty.catpospoint.domain.repository.PointRepository
import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import com.cresoty.catpospoint.domain.repository.UpdateRepository
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

    @Binds
    @Singleton
    abstract fun bindUpdateRepository(repo: UpdateRepositoryImpl): UpdateRepository

    @Binds
    @Singleton
    abstract fun bindSocketResponseRepository(repo: SocketResponseRepositoryImpl): SocketResponseRepository
}