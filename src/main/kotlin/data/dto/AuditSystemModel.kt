package data.dto

data class AuditSystemModel(
    val id: String,
    val entityType: String,
    val entityTypeId: String,
    val description: String,
    val userId: String,
    val dateTime: String
)
