package org.example.data.models

data class AuditSystem(
    val id: String,
    val auditSystemType: String,
    val entityId: String,
    val description: String,
    val changedBy: String,
    val dateTime: String
)
