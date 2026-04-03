package com.cresoty.catpospoint.remote.api.interceptor

import com.cresoty.catpospoint.domain.repository.AppSupportAuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** 저장된 토큰이 있으면 Authorization 헤더를 자동으로 추가하는 인터셉터 */
class AuthInterceptor @Inject constructor(
    private val authRepository: AppSupportAuthRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            runCatching { authRepository.getValidToken() }.getOrNull()
        }
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
