package logic.model.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Task(
    val id: UUID = UUID.randomUUID(),
    val name: String?,
    val description: String?,
    val stateId: String?,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime
)
