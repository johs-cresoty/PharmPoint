package com.cresoty.catpospoint.domain.repository

interface AppSupportAuthRepository {
    /** 저장된 토큰 반환. 없으면 로그인 후 반환. */
    suspend fun getValidToken(): String
    /** 토큰 강제 갱신 (401 응답 시 호출). 리프레시 실패 시 재로그인. */
    suspend fun forceRefresh(): String
    suspend fun logout()
}
