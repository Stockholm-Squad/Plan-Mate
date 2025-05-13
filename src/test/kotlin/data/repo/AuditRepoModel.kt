package data.repo

import data.dto.AuditDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import java.util.UUID

val auditId = UUID.fromString("4c254165-fe54-4dc9-911a-dc309273e749")
val entityId = UUID.fromString("e058fc70-e133-43e1-850e-828cf1ef765c")
val userId = UUID.fromString("b51f0645-0b89-453d-9ed7-277350e2d58d")
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