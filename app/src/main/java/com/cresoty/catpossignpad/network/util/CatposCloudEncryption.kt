package com.cresoty.catpossignpad.network.util

import android.text.TextUtils
import java.sql.Timestamp
import java.text.SimpleDateFormat

class CatposCloudEncryption: EncryptionProtocol {
    private val secretKey = "crecat"

    override fun encrypt(text: String): EncryptedData {
        var newText = text
        val key = secretKey + SimpleDateFormat("dd").format(Timestamp(System.currentTimeMillis()))
        if (TextUtils.isEmpty(text)) {
            newText = ""
        }
        var result = ""
        var resultLength = ""
        try {
            val textLength = newText.length
            if (textLength == 0) return EncryptedData.empty
            val textArray = getHexString(newText).split("\\|".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val keyArray = getHexString(key).split("\\|".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            var tmpA = 0
            var tmpB = 0
            var tmp = ""
            var j = 0
            for (i in textArray.indices) {
                tmpA = getHexa(textArray[i])
                tmpB = getHexa(keyArray[j])
                tmp = (tmpA xor tmpB).toString() + ""
                result += tmp
                resultLength += tmp.length
                j++
                if (j == keyArray.size) j = 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return EncryptedData(data = "$result^$resultLength")
    }

    override fun decrypt(encryptedData: EncryptedData): String {
        val text = encryptedData.data
        if (text.isEmpty()) return ""
        val key = secretKey + SimpleDateFormat("dd").format(Timestamp(System.currentTimeMillis()))
        var hexaString = ""
        var result = ""
        try {
            val delimiterPos = text.indexOf("^")
            if (delimiterPos <= 0) return encryptedData.data
            val tmpA = text.substring(0, delimiterPos)
            val tmpB = text.substring(delimiterPos + 1)
            val textLength = tmpB.length
            val keyLength = key.length
            val arr = arrayOfNulls<String>(textLength)
            var j = 0
            var size: Int
            for (i in 0 until textLength) {
                size = tmpB.substring(i, i + 1).toInt()
                arr[i] = tmpA.substring(j, j + size)
                j = j + size
            }
            val keyArray = arrayOfNulls<String>(keyLength)
            for (i in 0 until keyLength) keyArray[i] = key.substring(i, i + 1)
            var tmp: Int
            j = 0
            for (i in 0 until textLength) {
                tmp = arr[i]!!.toInt() xor keyArray[j]!![0].code
                hexaString += Integer.toHexString(tmp)
                j++
                if (j == keyLength) j = 0
            }
            result = String(
                org.apache.commons.codec.binary.Hex.decodeHex(hexaString.toCharArray()),
                charset("UTF-8")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    @Throws(Exception::class)
    private fun getHexString(str: String): String {
        // string -> byte array
        val inputBytes = str.toByteArray(charset("UTF-8"))
        var hexString = ""
        for (b in inputBytes) {
            hexString += Integer.toString(b.toInt() and 0xF0 shr 4, 16)
            hexString += Integer.toString(b.toInt() and 0x0F, 16)
            hexString += "|"
        }
        return hexString
    }

    @Throws(Exception::class)
    private fun getHexa(str: String): Int {
        return str.toInt(16)
    }
}