package data.mapper

import data.dto.AuditDto
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.utils.DateHandlerImp
import org.example.logic.utils.toSafeUUID


fun AuditDto.mapToAuditEntity(): Audit? {
    return Audit(
        id = id.toSafeUUID() ?: return null,
        entityType = EntityType.getAuditType(entityType),
        entityTypeId = entityTypeId.toSafeUUID() ?: return null,
        description = description,
        userId = userId.toSafeUUID() ?: return null,
        dateTime = DateHandlerImp().getLocalDateTimeFromString(dateTime)
    )
}

fun Audit.mapToAuditModel(): AuditDto {
    return AuditDto(
        id = id.toString(),
        entityType = entityType.toString(),
        entityTypeId = entityTypeId.toString(),
        description = description,
        userId = userId.toString(),
        dateTime = dateTime.toString()
    )
}
