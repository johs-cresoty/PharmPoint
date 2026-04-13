package com.cresoty.catpospoint.domain.repository

interface AppSupportAuthRepository {
    /** 저장된 토큰 반환. 없으면 로그인 후 반환. */
    suspend fun getValidToken(): String
    /** 토큰 강제 갱신 (401 응답 시 호출). 리프레시 실패 시 재로그인. */
    suspend fun forceRefresh(): String
    suspend fun logout()
    /** 기존 토큰을 제거하고 ConfigKey.BIZ_NO / PASSWORD로 새로 로그인. 약국정보 저장 후 호출. */
    suspend fun reLogin()
    /** 사업자번호로 유효한 약국인지 검증. (valid, message) 반환. */
    suspend fun validatePharmacy(bizNo: String): Pair<Boolean, String>
}
