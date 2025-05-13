package org.example.logic.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


class DateHandlerImp : DateHandler {
    override fun getCurrentDateTime(): LocalDateTime {
        val now = Clock.System.now()
        val timeZone = TimeZone.Companion.currentSystemDefault()
        return now.toLocalDateTime(timeZone)
    }

    fun getLocalDateTimeFromString(date: String): LocalDateTime = try {
        LocalDateTime.Companion.parse(date)
    } catch (ex: IllegalArgumentException) {
        throw Exception("Invalid date format: $this")
    }
}