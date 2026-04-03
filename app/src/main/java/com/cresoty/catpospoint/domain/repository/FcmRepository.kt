package com.cresoty.catpospoint.domain.repository

interface FcmRepository {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
}
