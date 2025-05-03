package org.example.ui.utils

import kotlinx.datetime.LocalDateTime

interface DateHandler {
    fun getCurrentDateTime(): LocalDateTime
}