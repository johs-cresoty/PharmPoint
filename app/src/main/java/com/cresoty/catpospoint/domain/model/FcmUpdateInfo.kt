package com.cresoty.catpospoint.domain.model

/**
 * FCM 업데이트 알림 데이터.
 *
 * @param installUrl    APK 다운로드 URL
 * @param installVersion 배포된 버전 코드
 * @param targetVersion 롤백 대상 버전 코드. null 이면 일반 업데이트, non-null 이면 롤백.
 */
data class FcmUpdateInfo(
    val installUrl: String,
    val installVersion: Int,
    val targetVersion: Int?,
)
