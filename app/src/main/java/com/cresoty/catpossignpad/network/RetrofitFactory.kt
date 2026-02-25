package com.cresoty.catpossignpad.network

import android.util.Log
import com.cresoty.catpossignpad.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitFactory {

    object BaseURL {
        const val CATPOS_CLOUD = "http://catpos.co.kr:13922/"
        const val CATPOS_CLOUD_TEST = "http://dev.catpos.co.kr/"
    }

    companion object {
        private fun makeClient(interceptor: Interceptor): OkHttpClient {

            val builder = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor { message ->
                    Log.d("API_LOG", message)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                builder.addInterceptor(logging)
            }
            return builder.build()
        }

        private fun makeRetrofit(
            baseUrl: String,
            client: OkHttpClient,
            factory: retrofit2.Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(factory)
                .build()
        }

        fun catposRetrofit(isDev: Boolean, deviceName: String, mainAppVersion: String): Retrofit {
            val client = makeClient(CatposCloudInterceptor(deviceName, mainAppVersion))

            val gson = GsonBuilder()
                .setLenient()
//                .registerTypeAdapter(ApprovalListData::class.java, ApprovalListDataDeserializer()) // null 값이 포함되어 따로 처리
//                .registerTypeAdapter(ApprovalDrugItemData::class.java, ApprovalDrugItemDataDeserializer()) // null 값이 포함되어 따로 처리
                .create()

            //개발시 개발계 호출
            return makeRetrofit(
                baseUrl =
                    if (isDev) {
                        BaseURL.CATPOS_CLOUD_TEST
                    } else {
                        BaseURL.CATPOS_CLOUD
                    },
                client = client,
                factory = GsonConverterFactory.create(gson)
            )
        }
    }
}