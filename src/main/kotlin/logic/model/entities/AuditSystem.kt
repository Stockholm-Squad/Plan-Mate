package logic.model.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class AuditSystem(
    val id: String = UUID.randomUUID().toString(),
    val auditSystemType: String,
    val entityId: String,
    val changeDescription: String,
    val changedBy: String,
    val dateTime: String
)
