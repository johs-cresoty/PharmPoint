package com.cresoty.catpospoint.remote.api

import android.util.Log
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.remote.api.interceptor.CryptoInterceptor
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

fun createApiService(): CatposCloudApi {
    val gson = GsonBuilder()
        .setLenient()
        .create()

    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectionPool(ConnectionPool(5, KEEP_ALIVE_DURATION, TimeUnit.SECONDS))  // 추가

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor { Log.d("HTTP", it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            addInterceptor(logger)         // 암호화 전 실제 값 로그
            addInterceptor(CryptoInterceptor())
        } else {
            addInterceptor(CryptoInterceptor())
        }
    }.build()

//    val baseUrl = if (BuildConfig.DEBUG) BASE_URL_DEV else BASE_URL_PROD
    val baseUrl = BASE_URL_PROD

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(CatposCloudApi::class.java)
}