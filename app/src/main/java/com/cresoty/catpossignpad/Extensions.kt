package com.cresoty.catpossignpad

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.cresoty.catpossignpad.view.theme.transparent
import java.io.UnsupportedEncodingException
import java.text.DecimalFormat
import kotlin.experimental.or
import kotlin.experimental.xor

fun String.stringToMutableByte(): MutableList<Byte> =
    (this as java.lang.String).getBytes(Val.getKorCharset()).toMutableList()


fun String.toIntOrMax(): Int {
    val long = this.toLongOrNull() ?: return 0

    return when {
        long > Int.MAX_VALUE -> Int.MAX_VALUE
        long < Int.MIN_VALUE -> Int.MIN_VALUE
        else -> long.toInt()
    }

}

fun isDigit(b: Byte): Boolean = b in 0x30..0x39

fun ByteArray.splitTelegram(delimeter: Byte): ArrayList<ByteArray> {
    val data = this
    val dataList = ArrayList<ByteArray>()
    var nStartIdx = 0
    var splitData: ByteArray
    for (i in data.indices) {
        val temp = data[i]
        if (temp == delimeter) {
            if (nStartIdx == i) {   // EmptyParts
                splitData = ByteArray(1)
                splitData[0] = 0x00
            } else {
                splitData = data.copyOfRange(nStartIdx, nStartIdx + (i - nStartIdx))
            }
            dataList.add(splitData)
            nStartIdx = i + 1
        }
        if (i == data.lastIndex) {
            splitData = if (data[i - 1] == Val.COMM_ETX) {
                data.copyOfRange(nStartIdx, nStartIdx + (i - nStartIdx - 1))
            } else {
                data.copyOfRange(nStartIdx, nStartIdx + (i - nStartIdx + 1))
            }
            dataList.add(splitData)
        }
    }
    return dataList
}

fun ByteArray.findAsciiControlChar(find: Byte): Int {
    var ret = 0

    for (index in this.indices) {
        if (this[index] == find) {
            ret = index
            break
        }
    }
    return ret
}

@Composable
fun Float.px2dp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@px2dp.toDp() }
}

@Composable
fun Float.px2sp(): TextUnit {
    val density = LocalDensity.current
    val screenDp = LocalConfiguration.current.screenWidthDp

    //실제 장비 가로폭 dp / 피그마 가로폭 800
    val scale = screenDp / 800f
    return with(density) { (this@px2sp * scale).sp }
}

fun String.toBizNoFormat(): String {
    val digits = this.filter { it.isDigit() }
    val ret = "${digits.substring(0, 3)}-${digits.substring(3, 5)}-${digits.substring(5, 10)}"

    return ret
}

fun Int.toDecimalString(): String {
    val decimal = DecimalFormat("#,###")
    return decimal.format(this)
}

fun String.toDecimalString(): String {
    this.toIntOrNull()?.let {
        return DecimalFormat("#,###").format(it)
    }
    return "0"
}

fun String.safeSubString(start: Int, endExclusive: Int = this.length): String {
    val s = this
    if (start < 0 || endExclusive < start) return ""
    if (s.length <= start) return ""
    val end = minOf(endExclusive, s.length)
    return s.substring(start, end)
}

// startIndex만 받는 버전 (끝까지 잘라오기)
fun String.safeSubString(start: Int): String {
    val s = this
    if (start < 0) return ""
    if (s.length <= start) return ""
    return s.substring(start, s.length)
}

fun String.maskingPhoneNumber(): List<String> {
    val data = this.filter { it.isDigit() }

    val start = data.safeSubString(0, 3)
    if (data.length <= 3) return listOf(start, "", "")

    val rest = data.drop(3)
    val middle = rest.safeSubString(0, 4)
    val end = rest.safeSubString(4, 8)

    val result = mutableListOf<String>(start)

    val maskedMiddle = "*".repeat(middle.length)
    result.add(maskedMiddle)

    //추후에 휴대폰번호 끝 4자리도 마스킹 필요할 경우 사용
    val maskedEnd = "*".repeat(end.length)
//    result.add(maskedEnd)
    result.add(end)

    return result
}

fun Map<String, Any>.safeGetString(key: String): String {
    return this[key]?.toString() ?: ""
}

fun ByteArray.byte2String(): String {
    val data = this
    if (data.isNotEmpty() && data[0].toInt() == 0x00) {
        return ""
    }
    try {
        return data.toString(Val.getKorCharset())
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }

    return ""
}

fun compareBytArray(
    data1: ByteArray,
    data2: ByteArray,
    nData1Start: Int,
    nData2Start: Int,
    nLength: Int,
): Boolean {
    var nData2Start = nData2Start
    for (i in nData1Start until nLength) {
        if (data1[i] != data2[nData2Start++]) {
            return false
        }
    }
    return true
}

fun getLRC(data: ByteArray, byLength: Int): Byte {
    var byLRC: Byte = 0
    for (i in 1 until byLength) {
        byLRC = byLRC.xor(data[i])
    }

    byLRC = byLRC.or(0x20.toByte()) // 0x20은 Int로 취급되므로 Byte로 변환

    return byLRC
}

fun Modifier.insetShadow(
    shape: Shape,
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.25f),
    start: Boolean = true,
    top: Boolean = true,
    end: Boolean = true,
    bottom: Boolean = true,
): Modifier = this.drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val clipPath = Path().apply { addOutline(outline) } // ✅ Outline -> Path
    val r = radius.toPx().coerceAtLeast(1f)

    val topBrush = Brush.verticalGradient(
        0f to color,
        1f to transparent,
        endY = r
    )
    val startBrush = Brush.horizontalGradient(
        0f to color,
        1f to transparent,
        endX = r
    )
    val bottomBrush = Brush.verticalGradient(
        0f to transparent,
        1f to color.copy(alpha = color.alpha * 0.5f),
        startY = size.height - r,
        endY = size.height
    )
    val endBrush = Brush.horizontalGradient(
        0f to transparent,
        1f to color.copy(alpha = color.alpha * 0.5f),
        startX = size.width - r,
        endX = size.width
    )

    onDrawWithContent {
        drawContent()
        clipPath(clipPath) {
            if (top) drawRect(topBrush)
            if (start) drawRect(startBrush)
            if (bottom) drawRect(bottomBrush)
            if (end) drawRect(endBrush)
        }
    }
}

fun Activity.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.statusBars()) // 상태바만 숨김
        // 상태바 + 내비게이션바까지 전부 숨기려면:
        // controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // 스와이프로 잠깐 나타나게 하는 옵션입니다.
    }
}