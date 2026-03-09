package com.cresoty.catpospoint.dataresource

sealed class DataResource<out T> {
    data class Success<T>(val data: T) : DataResource<T>()
    data class Error(val throwable: Throwable) : DataResource<Nothing>()
    data object Loading : DataResource<Nothing>()
}
