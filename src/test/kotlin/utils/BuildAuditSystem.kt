package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType


fun createAuditSystem(
    id: String ,
    auditSystemType: AuditSystemType,
    entityId: String,
    changeDescription: String,
    changedBy: String
    ): AuditSystem {
        return AuditSystem(
            id = id,
            auditSystemType = auditSystemType.toString(),
            entityId = entityId,
            changeDescription = changeDescription,
            dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            changedBy = changedBy
        )
    }