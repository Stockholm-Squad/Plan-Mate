package data.repo

import data.dto.AuditDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import java.util.UUID

val auditId = UUID.randomUUID()
val entityId = UUID.randomUUID()
val userId = UUID.randomUUID()
val auditDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
val auditDto = AuditDto(
    id = auditId.toString(),
    entityType =EntityType.TASK.name,
    entityTypeId = entityId.toString(),
    description = "description",
    userId = userId.toString(),
        dateTime =auditDateTime.toString()
)
val audit = Audit(
    id = auditId,
    entityType = EntityType.TASK,
    entityTypeId = entityId,
    description = "description",
    userId = userId,
    dateTime = auditDateTime
)