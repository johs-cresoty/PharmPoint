package com.cresoty.catpossignpad.remote.api

import android.util.Log
import com.cresoty.catpossignpad.BuildConfig
import com.cresoty.catpossignpad.remote.api.CatposCloudApi
import com.cresoty.catpossignpad.remote.api.interceptor.CryptoInterceptor
import com.cresoty.catpossignpad.remote.constant.ServiceConstant.BASE_URL_DEV
import com.cresoty.catpossignpad.remote.constant.ServiceConstant.BASE_URL_PROD
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


private const val TIME_OUT = 30L


fun createApiService(): CatposCloudApi {
    val gson = GsonBuilder()
        .setLenient()
        .create()

    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        connectTimeout(TIME_OUT, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val plainLogger = HttpLoggingInterceptor { Log.d("HTTP_PLAIN", it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            val encryptedLogger = HttpLoggingInterceptor { Log.d("HTTP_ENCRYPTED", it) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }

            addInterceptor(plainLogger)          // 암호화 전 로그
            addInterceptor(CryptoInterceptor())
            addNetworkInterceptor(encryptedLogger) // 암호화 후 로그
        } else {
            addInterceptor(CryptoInterceptor())
        }
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