package com.cresoty.catpospoint.device

import android.os.Build
import com.cresoty.catpospoint.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 기기 및 앱 식별 정보를 제공하는 싱글톤.
 * API 요청 시 공통으로 사용되는 CMPTR_NAME, POS_VER 의 단일 출처.
 */
@Singleton
class DeviceInfoProvider @Inject constructor() {
    /** 기기 식별자 (CMPTR_NAME): "{Brand}_{Model}" */
    val computerName: String = "${Build.BRAND}_${Build.MODEL}"

    /** 앱 버전 (POS_VER) */
    val posVersion: String = BuildConfig.VERSION_NAME
}
