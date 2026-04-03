package com.cresoty.catpospoint.domain.repository

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    /** 앱 기기 정보(androidId, fcmToken, ip, 사업자번호, 버전코드, 플랫폼)를 서버에 등록한다. */
    suspend fun registerDevice()
    /** 기기 IP 주소가 실제로 변경될 때마다 Unit 을 emit 하는 Flow */
    fun observeIpChanges(): Flow<Unit>
}
