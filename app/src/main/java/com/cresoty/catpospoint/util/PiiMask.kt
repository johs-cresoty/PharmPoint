package com.cresoty.catpospoint.util

object PiiMask {
    fun phone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""
        val digits = phone.filter { it.isDigit() }
        return if (digits.length >= 4) "***${digits.takeLast(4)}" else "***"
    }
}
