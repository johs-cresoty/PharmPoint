package com.cresoty.catpospoint.domain.model

/**
 * 강제 업데이트 시 표시할 정보.
 *
 * @param installUrl    APK 다운로드 URL
 * @param messageTitle  업데이트 다이얼로그 제목
 * @param message       업데이트 다이얼로그 본문
 */
data class UpdateInfo(
    val installUrl: String,
    val messageTitle: String,
    val message: String,
)
