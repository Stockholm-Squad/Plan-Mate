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
    changedBy: String
    ): AuditSystem {
        return AuditSystem(
            id = id,
            entityType = entityType.toString(),
            entityId = entityId,
            changeDescription = changeDescription,
            dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            changedBy = changedBy
        )
    }