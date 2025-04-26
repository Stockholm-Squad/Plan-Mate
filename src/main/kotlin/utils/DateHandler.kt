package org.example.logic.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateHandler(){
    fun getCurrentDateTime(): LocalDateTime{
        val now = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        return now.toLocalDateTime(tz)
    }
}