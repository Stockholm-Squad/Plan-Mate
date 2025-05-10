package utils

import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import data.dto.AuditDto
import java.util.*


fun createAuditSystemEntity(
    id: UUID,
    entityType: EntityType,
    entityTypeId: UUID,
    userId: UUID,
    description: String,
    createdAt: LocalDateTime
): Audit = Audit(id = id, entityType = entityType, entityTypeId = entityTypeId, userId = userId, description = description, dateTime = createdAt)

fun createAuditSystemModel(
    id: String,
    entityType: String,
    entityId: String,
    userId: String,
    changeDescription: String,
    createdAt: String
): AuditDto = AuditDto(id, entityType, entityId, userId, changeDescription, createdAt)
