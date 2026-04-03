package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.domain.model.FcmUpdateInfo
import javax.inject.Inject

/**
 * FCM 업데이트 알림을 받았을 때 배너를 표시해야 하는지 판단한다.
 *
 * - targetVersion == null (일반 업데이트): installVersion > currentVersionCode 일 때 표시
 * - targetVersion != null (롤백):         targetVersion == currentVersionCode 일 때 표시
 */
class CheckFcmUpdateUseCase @Inject constructor() {
    operator fun invoke(update: FcmUpdateInfo, currentVersionCode: Int): Boolean =
        if (update.targetVersion == null) {
            update.installVersion > currentVersionCode
        } else {
            update.targetVersion == currentVersionCode
        }
}
