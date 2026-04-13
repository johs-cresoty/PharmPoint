package com.cresoty.catpospoint.remote.crypt

import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object CresotyCrypt {

    private const val SECRET_KEY = "abcdefghijklmnopqrstuvwxyz123456"

    private fun getDefaultKey(): String {
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        return "crecat${dateFormat.format(Date())}"
    }

    fun getEncode(plainDouble: Double): String {
        return getEncode(String.format("%.2f", plainDouble))
    }

    fun getEncode(plainLong: Long): String {
        return getEncode(plainLong.toString())
    }

    fun getEncode(plainInt: Int): String {
        return getEncode(plainInt.toString())
    }

    /**
     * 날짜 기반 XOR 암호화
     * 키가 날마다 바뀝니다. 오늘 암호화한 값을 내일 복호화하면 틀린 값이 나옵니다.
     */
    fun getEncode(plainText: String?): String {
        if (plainText.isNullOrEmpty()) return ""

        val key = getDefaultKey()

        // Java 원본과 동일하게 처리: split("\\|") 사용
        val hexText = getHexString(plainText)
        val hexKey = getHexString(key)

        val textArray = hexText.split(Regex("\\|")).filter { it.isNotEmpty() }
        val keyArray = hexKey.split(Regex("\\|")).filter { it.isNotEmpty() }

        val cypherText = StringBuilder()
        val cypherTextLength = StringBuilder()
        var j = 0

        for (i in textArray.indices) {
            val tmpA = textArray[i].toInt(16)
            val tmpB = keyArray[j].toInt(16)
            val tmpStr = (tmpA xor tmpB).toString()

            cypherText.append(tmpStr)
            cypherTextLength.append(tmpStr.length)

            if (++j == keyArray.size) j = 0
        }

        return "$cypherText^$cypherTextLength"
    }


    /**
     * 고정 키 AES-256 암호화
     *  키가 항상 동일하므로 언제 복호화해도 같은 원본이 나옵니다.
     */
    fun getAESEncode(plainText: String): String {
        val keyData = SECRET_KEY.toByteArray()
        val secureKey = SecretKeySpec(keyData, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val iv = SECRET_KEY.substring(0, 16)
        cipher.init(Cipher.ENCRYPT_MODE, secureKey, IvParameterSpec(iv.toByteArray()))

        val encryptText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptText, Base64.NO_WRAP)
    }

    fun getAESDecode(encryptedText: String): String {
        val keyData = SECRET_KEY.toByteArray()
        val secureKey = SecretKeySpec(keyData, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val iv = SECRET_KEY.substring(0, 16)
        cipher.init(Cipher.DECRYPT_MODE, secureKey, IvParameterSpec(iv.toByteArray()))

        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.NO_WRAP))
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun getDecode(cypherText: String): String {
        if (cypherText.isEmpty()) return ""

        val key = getDefaultKey()
        val delimiterPos = cypherText.indexOf("^")

        if (delimiterPos <= 0) return ""

        val splitText = cypherText.substring(0, delimiterPos)
        val splitKey = cypherText.substring(delimiterPos + 1)

        val splitTextLength = splitKey.length
        val splitKeyLength = key.length

        val textArr = Array(splitTextLength) { "" }
        var j = 0

        for (i in 0 until splitTextLength) {
            val size = splitKey.substring(i, i + 1).toInt()
            textArr[i] = splitText.substring(j, j + size)
            j += size
        }

        val keyArr = Array(splitKeyLength) { i -> key.substring(i, i + 1) }
        val hex = StringBuilder()
        j = 0

        for (i in 0 until splitTextLength) {
            hex.append(Integer.toHexString(textArr[i].toInt() xor keyArr[j][0].code))
            j++
            if (j == splitKeyLength) j = 0
        }

        return hex.toString().chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
            .toString(Charsets.UTF_8)
    }

    private fun getHexString(plainText: String): String {
        val inputBytes = plainText.toByteArray(Charsets.UTF_8)
        val hexString = StringBuilder()

        for (byte in inputBytes) {
            hexString.append(Integer.toString((byte.toInt() and 0xf0) shr 4, 16))
            hexString.append(Integer.toString(byte.toInt() and 0xf, 16))
            hexString.append("|")
        }

        return hexString.toString()
    }
}