package com.cresoty.catpossignpad.network.util

import com.cresoty.catpossignpad.network.util.EncryptionUtil.encrypt

data class EncryptedData(
    val data: String,
    var iv: String? = null,
    var mac: String? = null
) {
    companion object {
        val empty: EncryptedData = EncryptedData(data = "")
    }
}

interface EncryptionProtocol {
    fun encrypt(text: String): EncryptedData
    fun decrypt(encryptedData: EncryptedData): String
}

object EncryptionFactory {
    fun getEncryptionProtocol(): EncryptionProtocol {
        return CatposCloudEncryption()
    }
}

object EncryptionUtil {
    fun encrypt(text: String): EncryptedData {
        val encryptionProtocol = EncryptionFactory.getEncryptionProtocol()
        return encryptionProtocol.encrypt(text)
    }

    fun decrypt(encryptedData: EncryptedData): String {
        val encryptionProtocol = EncryptionFactory.getEncryptionProtocol()
        return encryptionProtocol.decrypt(encryptedData)
    }

    fun decrypt(text: String): String {
        return decrypt(EncryptedData(data = text))
    }
}

fun String.getEncryptionData(): String {
    return encrypt(this).data
}

fun Int.getEncryptionData(): String {
    return encrypt(this.toString()).data
}