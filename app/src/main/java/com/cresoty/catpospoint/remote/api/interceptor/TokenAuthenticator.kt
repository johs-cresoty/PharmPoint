package com.cresoty.catpospoint.remote.api.interceptor

import com.cresoty.catpospoint.domain.repository.AppSupportAuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/** 401 응답 시 토큰을 갱신하고 요청을 재시도하는 Authenticator */
class TokenAuthenticator @Inject constructor(
    private val authRepository: AppSupportAuthRepository,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 이미 재시도한 경우 무한 루프 방지
        if (response.priorResponse != null) return null

        val newToken = runBlocking {
            runCatching { authRepository.forceRefresh() }.getOrNull()
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}
