package utils

import data.dto.AuditDto
import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import java.util.*

fun buildAudit(
    id: UUID,
    entityType: EntityType,
    entityTypeId: UUID,
    userId: UUID,
    description: String,
    createdAt: LocalDateTime
): Audit = Audit(
    id = id,
    entityType = entityType,
    entityTypeId = entityTypeId,
    description = description,
    userId = userId,
    dateTime = createdAt
)

fun buildAuditModel(
    id: String,
    entityType: String,
    entityId: String,
    userId: String,
    changeDescription: String,
    createdAt: String
): AuditDto = AuditDto(
    id = id,
    entityType = entityType,
    entityTypeId = entityId,
    userId = userId,
    description = changeDescription,
    dateTime = createdAt,
)
