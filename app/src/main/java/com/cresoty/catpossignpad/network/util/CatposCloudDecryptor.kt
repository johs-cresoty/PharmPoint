package com.cresoty.catpossignpad.network.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class CatposCloudDecryptor : TypeAdapter<String>() {
    override fun write(out: JsonWriter?, value: String?) {
        out?.value(value ?: "")
    }

    override fun read(reader: JsonReader?): String {
        return when (reader?.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                ""
            }
            JsonToken.STRING -> {
                val value = reader.nextString()
                if (value.isEmpty()) {
                    ""
                } else {
                    try {
                        EncryptionUtil.decrypt(value)
                    } catch (e: Exception) {
                        value
                    }
                }
            }
            else -> {
                reader?.skipValue()
                ""
            }
        } ?: ""
    }
}