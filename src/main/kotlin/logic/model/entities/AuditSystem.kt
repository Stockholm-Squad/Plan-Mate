package logic.model.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class AuditSystem(
    val id: UUID = UUID.randomUUID(),
    val auditSystemType: AuditSystemType,
    val entityId: String,
    val description: String,
    val changedBy: User,
    val dateTime: LocalDateTime
)
