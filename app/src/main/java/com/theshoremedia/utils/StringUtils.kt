package com.theshoremedia.utils

import java.util.*

/**
 * @author- Nitin Khanna
 * @date -
 */
object StringUtils {
    private const val TIME_REGEX = "^(1[0-2]|0?[1-9]):([0-5][0-9]) ([AaPp][Mm])\$"
    const val DATE_REGEX =
        "^(today\$|yesterday\$|(([0-9])|([0-2][0-9])|([3][0-1]))\\ (january|february|march|april|may|july|august|september|october|november|december)\\ \\d{4}\$)"

    fun isTimeView(value: String?): Boolean {
        if (value.isNullOrEmpty()) return false
        val pattern = Regex(TIME_REGEX)
        return pattern.containsMatchIn(value.toLowerCase(Locale.ROOT))
    }

    fun isDateView(value: String?): Boolean {
        if (value.isNullOrEmpty()) return false
        val pattern = Regex(DATE_REGEX)
        return pattern.containsMatchIn(value.toLowerCase(Locale.ROOT))
    }
}