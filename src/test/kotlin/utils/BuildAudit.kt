package utils

import data.dto.AuditDto
import kotlinx.datetime.LocalDateTime
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import java.util.*

fun buildAudit(
    id: UUID = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa1"),
    entityType: EntityType = EntityType.PROJECT,
    entityTypeId: UUID = UUID.fromString("e3a85f64-5717-4562-b3fc-2c963f66dabc"),
    userId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1"),
    description: String = "Added",
    createdAt: LocalDateTime = DateHandlerImp().getCurrentDateTime()
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
