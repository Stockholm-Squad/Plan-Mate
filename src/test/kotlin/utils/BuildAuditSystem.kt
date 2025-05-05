package utils

import kotlinx.datetime.LocalDateTime
import logic.models.entities.AuditSystem
import logic.models.entities.EntityType
import org.example.data.models.AuditSystemModel
import java.util.*


fun createAuditSystemEntity(
    id: UUID,
    entityType: EntityType,
    entityTypeId: UUID,
    userId: UUID,
    description: String,
    createdAt: LocalDateTime
): AuditSystem = AuditSystem(id = id, entityType = entityType, entityTypeId = entityTypeId, userId = userId, description = description, dateTime = createdAt)

fun createAuditSystemModel(
    id: String,
    entityType: String,
    entityId: String,
    userId: String,
    changeDescription: String,
    createdAt: String
): AuditSystemModel = AuditSystemModel(id, entityType, entityId, userId, changeDescription, createdAt)
