package com.cresoty.catpospoint.domain.fcm

import kotlinx.coroutines.flow.SharedFlow

interface FcmEventRepository {
    val events: SharedFlow<FcmEvent>
    suspend fun emit(event: FcmEvent)
}
