package com.cresoty.catpossignpad.network

import android.util.Log
import com.cresoty.catpossignpad.network.util.EncryptionUtil
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber

class CatposCloudInterceptor(
    private val deviceName : String,
    private val mainAppVersion : String
    ): Interceptor {

    private val encryptedRegex = Regex("^[^^]+\\^[^^]+$")

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        when(originalRequest.method()) {
            "GET", "DELETE" -> {
                val originalUrl = originalRequest.url()
                val newUrlBuilder = originalUrl.newBuilder()

//                val mainAppVersion = Class.forName("com.cresoty.catpossignpad.BuildConfig")
//                    .getField("VERSION_NAME").get(null) as String

                val commonParams =
                        mapOf(
                            "CMPTR_NAME" to deviceName,
                            "POS_VER" to mainAppVersion,
                            "POS_GUBN" to "SP",
                        )

                commonParams.forEach { (name, value) ->
                    newUrlBuilder.addQueryParameter(name, value)
                }

                val fullUrl = newUrlBuilder.build()
                val encryptedUrlBuilder = fullUrl.newBuilder()

            Log.d("@#@#", "[${originalRequest.method()}] request plain url: ${fullUrl}")

                fullUrl.queryParameterNames().forEach { name ->
                    val value = fullUrl.queryParameter(name)
                    value?.let {
                        val encryptedValue = EncryptionUtil.encrypt(it)
                        encryptedUrlBuilder.setQueryParameter(name, encryptedValue.data)
                    }
                }

                val encryptedUrl = encryptedUrlBuilder.build()
                val newRequest = originalRequest.newBuilder()
                    .url(encryptedUrl)
                    .build()

                Log.d("@#@#", "[${originalRequest.method()}] request encrypted url: ${newRequest.url()}")

                val response = chain.proceed(newRequest)

                return handleResponse(response)
            }
            else -> {

                val originalBody = originalRequest.body()

                val newRequest = originalRequest.newBuilder()
                    .method(originalRequest.method(), originalBody)
                    .header("Content-Type", "application/json")
                    .build()

                val buffer = okio.Buffer()
                originalBody?.writeTo(buffer)
                val requestBody = buffer.readUtf8()

                Log.d("@#@#", "[${originalRequest.method()}] request url: ${originalRequest.url()}")
//                Log.d("@#@#", "[${originalRequest.method()}] request encrypted body: $requestBody")

                val response = chain.proceed(newRequest)

                return handleResponse(response)

            }
        }

        return chain.proceed(originalRequest)
    }

    private fun handleResponse(response: Response): Response {
        val gson = Gson()
        val originalBody = response.body() ?: return response
        val contentType  = originalBody.contentType()
        val content = originalBody.string()

        val decryptedJson = runCatching {
            val parsed = JsonParser.parseString(content)

            gson.toJson(decryptElement(parsed))
        }.getOrElse {
            Timber.e(it, "Decryption failed")
            content
        }

        Log.d("@#@#", "[${response.request().method()}] response decrypted body : $decryptedJson")

        return response.newBuilder()
            .body(ResponseBody.create(contentType, decryptedJson))
            .build()
    }

    private fun decryptElement(elem: JsonElement, key : String? = null): JsonElement {
        if(elem.isJsonNull) return JsonPrimitive("")

        return when {
            elem.isJsonObject -> {
                val obj = JsonObject()
                elem.asJsonObject.entrySet().forEach { (k, v) ->
                    obj.add(k, decryptElement(v))
                }
                obj
            }
            elem.isJsonArray -> {
                val src = elem.asJsonArray

                // [null] 또는 [null, null] 같이 "전부 null"이면 []
                val allNull = src.size() > 0 && src.all { it.isJsonNull }
                if (allNull) return JsonArray()

                val arr = JsonArray()
                src.forEach { item ->
                    // 원소가 null이면 스킵해서 결과 배열에 넣지 않음 (원하시면 유지로 바꿀 수 있음)
                    if (!item.isJsonNull) {
                        arr.add(decryptElement(item, key))
                    }
                }
                arr
//                val arr = JsonArray()
//                elem.asJsonArray.forEach {
//                    arr.add(decryptElement(it))
//                }
//                arr
            }
            elem.isJsonPrimitive && elem.asJsonPrimitive.isString -> {
                val str = elem.asString
                // “비캐럿문자+캐럿+비캐럿문자” 형태일 때만 decrypt
                if (encryptedRegex.matches(str)) {
                    JsonPrimitive(
                        EncryptionUtil.decrypt(str)
                    )
                } else {
                    elem
                }
            }
            else -> elem
        }
    }

//    private fun handleResponse(response: Response): Response {
//        response.body()?.let { responseBody ->
//            val contentType = responseBody.contentType()
//            val content = responseBody.string()
//
//            try {
//                val decrypted =
//                    content.replace("\"[^\"]+\\^[^\"]+\"".toRegex()) { matchResult ->
//                        val encryptedValue = matchResult.value.trim('"') // "숫자^숫자" 형태의 전체 값
//                        "\"${
//                            EncryptionUtil.decrypt(
//                                encryptedValue,
//                                EncryptionType.CATPOS_CLOUD
//                            )
//                        }\""
//                    }
//                Timber.d("[${response.request().method()}] response body: $decrypted")
//            } catch (e: Exception) {
//                Timber.e(e, "Decryption failed")
//                Timber.d("[${response.request().method()}] response body: $content")
//            }
//
//            return response.newBuilder()
//                .body(ResponseBody.create(contentType, content))
//                .build()
//        }
//
//        return response
//    }
}