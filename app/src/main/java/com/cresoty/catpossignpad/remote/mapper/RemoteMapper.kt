package com.cresoty.catpossignpad.remote.mapper

interface RemoteMapper<DataModel> {
    fun toData(): DataModel
}