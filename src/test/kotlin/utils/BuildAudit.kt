package utils

import data.dto.AuditDto
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import java.util.*

fun buildAudit(
    id: UUID = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa1"),
    entityType: EntityType = EntityType.PROJECT,
    entityTypeId: UUID = UUID.fromString("e3a85f64-5717-4562-b3fc-2c963f66dabc"),
    userId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1"),
    description: String = "Added",
    createdAt: LocalDateTime = (Instant.parse("2024-05-01T10:00:00Z")).toLocalDateTime(TimeZone.currentSystemDefault()),
): Audit = Audit(
    id = id,
    entityType = entityType,
    entityTypeId = entityTypeId,
    description = description,
    userId = userId,
    dateTime = createdAt
)

fun buildAuditModel(
    id: String = "f3a85f64-5717-4562-b3fc-2c963f66bfa1",
    entityType: String = EntityType.PROJECT.name,
    entityTypeId: String = "e3a85f64-5717-4562-b3fc-2c963f66dabc",
    userId: String = "a3a85f64-5717-4562-b3fc-2c963f66abc1",
    description: String = "Added",
    createdAt: String = "2024-05-01T10:00:00Z",
): AuditDto = AuditDto(
    id = id,
    entityType = entityType,
    entityTypeId = entityTypeId,
    userId = userId,
    description = description,
    dateTime = createdAt,
)