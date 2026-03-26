package com.cresoty.catpospoint.data.socket.di

import com.cresoty.catpospoint.data.socket.SocketEventRepositoryImpl
import com.cresoty.catpospoint.domain.socket.SocketEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SocketModule {

    @Binds
    @Singleton
    abstract fun bindSocketEventRepository(
        impl: SocketEventRepositoryImpl,
    ): SocketEventRepository
}
