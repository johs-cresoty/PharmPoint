package com.cresoty.catpospoint.remote.mapper

interface RemoteMapper<DataModel> {
    fun toData(): DataModel
}