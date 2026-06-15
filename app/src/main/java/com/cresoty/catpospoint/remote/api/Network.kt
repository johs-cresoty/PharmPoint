package com.cresoty.catpospoint.remote.api

import android.util.Log
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.remote.api.interceptor.AuthInterceptor
import com.cresoty.catpospoint.remote.api.interceptor.CrashReportingInterceptor
import com.cresoty.catpospoint.remote.api.interceptor.CryptoInterceptor
import com.cresoty.catpospoint.remote.api.interceptor.TokenAuthenticator
import com.cresoty.catpospoint.remote.constant.ServiceConstant.BASE_URL_DEV
import com.cresoty.catpospoint.remote.constant.ServiceConstant.BASE_URL_PROD
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


private const val TIME_OUT = 30L
private const val KEEP_ALIVE_DURATION = 2L  // 서버 timeout(3초)보다 짧게

/** 인증 헤더 불필요한 app-support 인증 전용 Retrofit (login / refresh / logout) */
fun createAppSupportAuthApiService(): AppSupportAuthApi {
    val gson = GsonBuilder().setLenient().disableHtmlEscaping().create()

    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        addInterceptor(CrashReportingInterceptor())
        if (BuildConfig.LOG_HTTP) {
            val logger = HttpLoggingInterceptor { Log.d("HTTP_APP_SUPPORT_AUTH", it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            addInterceptor(logger)
        }
    }.build()

    val baseUrl = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AppSupportAuthApi::class.java)
}

/** AuthInterceptor + TokenAuthenticator 가 적용된 app-support 비즈니스 API (registerDevice / checkAppVersion) */
fun createAppSupportApiService(
    authInterceptor: AuthInterceptor,
    tokenAuthenticator: TokenAuthenticator,
): AppSupportApi {
    val gson = GsonBuilder().setLenient().disableHtmlEscaping().create()

    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        addInterceptor(authInterceptor)
        authenticator(tokenAuthenticator)
        addInterceptor(CrashReportingInterceptor())
        if (BuildConfig.LOG_HTTP) {
            val logger = HttpLoggingInterceptor { Log.d("HTTP_APP_SUPPORT", it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            addInterceptor(logger)
        }
    }.build()

    val baseUrl = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(AppSupportApi::class.java)
}

fun createApiService(): CatposCloudApi {
    val gson = GsonBuilder()
        .setLenient()
        .disableHtmlEscaping()
        .create()

    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectionPool(ConnectionPool(5, KEEP_ALIVE_DURATION, TimeUnit.SECONDS))

        addInterceptor(CrashReportingInterceptor())
        if (BuildConfig.LOG_HTTP) {
            val logger = HttpLoggingInterceptor { Log.d("HTTP", it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            addInterceptor(logger)         // 암호화 전 실제 값 로그
        }
        addInterceptor(CryptoInterceptor())
    }.build()

    val baseUrl = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(CatposCloudApi::class.java)
}
