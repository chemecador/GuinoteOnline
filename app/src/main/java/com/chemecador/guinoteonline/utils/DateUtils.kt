package com.chemecador.guinoteonline.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateUtils {

    private fun getCurrentDayFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    private fun getCurrentDateTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    fun getCurrentDateTimeMillis(): String {
        val currentDateTime = ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
        return currentDateTime.format(getCurrentDateTimeFormatter())
    }

    fun getCurrentDay(): String {
        val currentDateTime = ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
        return currentDateTime.format(getCurrentDayFormatter())
    }
}
