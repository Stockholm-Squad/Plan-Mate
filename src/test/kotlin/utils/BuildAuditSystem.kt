package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType


fun createAuditSystem(
    id: String,
    entityType: EntityType,
    entityId: String,
    changeDescription: String,
    userId: String
    ): AuditSystem {
        return AuditSystem(
            id = id,
            entityType = entityType.toString(),
            entityTypeId = entityId,
            description = changeDescription,
            dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            userId = userId
        )
    }