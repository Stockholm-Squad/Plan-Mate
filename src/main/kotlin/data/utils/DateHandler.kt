package org.example.data.utils

import kotlinx.datetime.LocalDateTime

interface DateHandler {
    fun getCurrentDateTime(): LocalDateTime
}
