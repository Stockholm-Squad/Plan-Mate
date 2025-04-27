package logic.model.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val description: String,
    val state: State,
    val assignedUser: User,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime,
)
