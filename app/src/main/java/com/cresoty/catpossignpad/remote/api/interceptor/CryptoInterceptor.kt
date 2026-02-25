package com.cresoty.catpossignpad.remote.api.interceptor


import com.cresoty.catpossignpad.remote.crypt.CresotyCrypt
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject

class CryptoInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = when (originalRequest.method) {
            "GET", "DELETE" -> encryptGetRequest(originalRequest)
            "POST", "PUT", "PATCH" -> encryptPostRequest(originalRequest)
            else -> originalRequest
        }

        val response = chain.proceed(newRequest)
        return decryptResponse(response)
    }

    private fun encryptGetRequest(request: Request): Request {
        val originalUrl = request.url
        val newUrlBuilder = originalUrl.newBuilder()

        originalUrl.queryParameterNames.forEach { paramName ->
            val paramValue = originalUrl.queryParameter(paramName)
            if (paramValue != null) {
                newUrlBuilder.removeAllQueryParameters(paramName)
                newUrlBuilder.addQueryParameter(paramName, CresotyCrypt.getEncode(paramValue))
            }
        }

        return request.newBuilder()
            .url(newUrlBuilder.build())
            .build()
    }

    private fun encryptPostRequest(request: Request): Request {
        val requestBody = request.body ?: return request
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        val originalJson = buffer.readUtf8()

        return try {
            val encryptedJson = encryptJsonObject(JSONObject(originalJson))
            val newBody = encryptedJson.toString()
                .toRequestBody("application/json; charset=UTF-8".toMediaType())

            request.newBuilder()
                .method(request.method, newBody)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            request
        }
    }

    private fun encryptJsonObject(jsonObject: JSONObject): JSONObject {
        val result = JSONObject()
        jsonObject.keys().forEach { key ->
            val value = jsonObject.get(key)
            result.put(key, when (value) {
                is JSONObject -> encryptJsonObject(value)
                is JSONArray -> encryptJsonArray(value)
                is String -> CresotyCrypt.getEncode(value)
                else -> value
            })
        }
        return result
    }

    private fun encryptJsonArray(jsonArray: JSONArray): JSONArray {
        val result = JSONArray()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.get(i)
            result.put(when (value) {
                is JSONObject -> encryptJsonObject(value)
                is JSONArray -> encryptJsonArray(value)
                is String -> CresotyCrypt.getEncode(value)
                else -> value
            })
        }
        return result
    }

    private fun decryptResponse(response: Response): Response {
        val responseBody = response.body ?: return response
        val encryptedBody = responseBody.string()

        return try {
            val decryptedJson = decryptJsonObject(JSONObject(encryptedBody))
            val contentType = responseBody.contentType() ?: "application/json".toMediaType()

            response.newBuilder()
                .body(decryptedJson.toString().toResponseBody(contentType))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            response.newBuilder()
                .body(encryptedBody.toResponseBody(responseBody.contentType()))
                .build()
        }
    }

    private fun decryptJsonObject(jsonObject: JSONObject): JSONObject {
        val result = JSONObject()
        jsonObject.keys().forEach { key ->
            val value = jsonObject.get(key)
            result.put(key, when (value) {
                is JSONObject -> decryptJsonObject(value)
                is JSONArray -> decryptJsonArray(value)
                is String -> if (value.contains("^")) {
                    try { CresotyCrypt.getDecode(value) } catch (e: Exception) { value }
                } else value
                else -> value
            })
        }
        return result
    }

    private fun decryptJsonArray(jsonArray: JSONArray): JSONArray {
        val result = JSONArray()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.get(i)
            result.put(when (value) {
                is JSONObject -> decryptJsonObject(value)
                is JSONArray -> decryptJsonArray(value)
                is String -> if (value.contains("^")) {
                    try { CresotyCrypt.getDecode(value) } catch (e: Exception) { value }
                } else value
                else -> value
            })
        }
        return result
    }
}