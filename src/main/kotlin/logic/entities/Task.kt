package org.example.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Task(
    val id: UUID = UUID.randomUUID(),
    val projectTitle: String,
    val title: String,
    val description: String,
    val stateId: UUID,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime
)
