package org.example.data.models

data class AuditSystem(
    val id: String,
    val auditSystemType: String,
    val entityTypeId: String,
    val description: String,
    val userId: String,
    val dateTime: String
)
