package org.example.data.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateHandlerImp : DateHandler {
    override fun getCurrentDateTime(): LocalDateTime {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        return now.toLocalDateTime(timeZone)
    }
}