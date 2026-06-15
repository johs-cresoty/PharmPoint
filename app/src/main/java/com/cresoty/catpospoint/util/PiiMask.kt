package com.cresoty.catpospoint.util

object PiiMask {
    fun phone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""
        val digits = phone.filter { it.isDigit() }
        return if (digits.length >= 4) "***${digits.takeLast(4)}" else "***"
    }

    /**
     * JSON 문자열에서 민감 필드 값을 *** 로 치환. 형식 검증 없이 정규식 기반.
     * 마스킹 대상: password, token, refreshToken
     */
    fun maskSensitiveJsonFields(body: String?): String {
        if (body.isNullOrEmpty()) return ""
        var masked: String = body
        listOf("password", "token", "refreshToken").forEach { field ->
            masked = masked.replace(
                Regex("(\"$field\"\\s*:\\s*)\"[^\"]*\""),
                "$1\"***\"",
            )
        }
        return masked
    }
}
