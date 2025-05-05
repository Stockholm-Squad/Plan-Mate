package logic.models.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class AuditSystem(
    val id: UUID = UUID.randomUUID(),
    val entityType: EntityType,
    val entityTypeId: UUID,
    val description: String,
    val userId: UUID,
    val dateTime: LocalDateTime
)
