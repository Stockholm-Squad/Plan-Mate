package utils

import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import data.dto.AuditSystemDto
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
): AuditSystemDto = AuditSystemDto(id, entityType, entityId, userId, changeDescription, createdAt)
