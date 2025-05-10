package data.mapper

import data.dto.AuditDto
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.utils.toSafeUUID


fun AuditDto.mapToAuditEntity(): Audit? {
    return Audit(
        id.toSafeUUID() ?: return null,
        entityType = getAuditType(entityType) ?: return null,
        entityTypeId = entityTypeId.toSafeUUID() ?: return null,
        description = description,
        userId = userId.toSafeUUID() ?: return null,
        dateTime = DateHandlerImp().getLocalDateTimeFromString(dateTime)
    )
}

fun Audit.mapToAuditModel(): AuditDto {
    return AuditDto(
        id.toString(),
        entityType = entityType.toString(),
        entityTypeId = entityTypeId.toString(),
        description = description,
        userId = userId.toString(),
        dateTime = dateTime.toString()
    )
}

fun getAuditType(auditSystem: String): EntityType? {
    return when {
        auditSystem.equals("TASK", ignoreCase = true) -> EntityType.TASK
        auditSystem.equals("PROJECT", ignoreCase = true) -> EntityType.PROJECT
        else -> null
    }
}
