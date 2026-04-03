package com.cresoty.catpospoint.data.fcm

import com.cresoty.catpospoint.domain.fcm.FcmEvent
import com.cresoty.catpospoint.domain.fcm.FcmEventRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmEventRepositoryImpl @Inject constructor() : FcmEventRepository {
    private val _events = MutableSharedFlow<FcmEvent>()
    override val events: SharedFlow<FcmEvent> = _events.asSharedFlow()
    override suspend fun emit(event: FcmEvent) = _events.emit(event)
}
