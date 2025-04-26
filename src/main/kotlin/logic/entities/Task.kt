package org.example.logic.entities

import kotlinx.datetime.LocalDateTime
data class Task(
    val id: String,
    val projectId: String,
    val name: String,
    val description: String,
    val state: State,
    val assignedUser: User,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime,
)
