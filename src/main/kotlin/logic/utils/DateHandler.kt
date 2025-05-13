package org.example.logic.utils

import kotlinx.datetime.LocalDateTime

interface DateHandler {
    fun getCurrentDateTime(): LocalDateTime
}