package org.example.data.models

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Task(
    val id: String ,
    val name: String?,
    val description: String?,
    val stateId: String?,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime
)
