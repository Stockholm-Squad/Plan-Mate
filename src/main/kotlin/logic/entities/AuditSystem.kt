package org.example.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AuditSystem(
    val id: String = UUID.randomUUID().toString(),
    val auditSystemType: AuditSystemType,
    val entityId: String,
    val changeDescription: String,
    val changedBy: String,
    val dateTime: LocalDateTime,
)
