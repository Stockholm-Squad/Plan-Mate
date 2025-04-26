package org.example.logic.entities

import kotlinx.datetime.LocalDateTime

data class AuditLog(
    val id: String,
    val auditSystemType: AuditSystemType,
    val type: String,
    val entityId: String,
    val changeDescription: String,
    val changedBy: String,
    val dateTime: LocalDateTime,
)
