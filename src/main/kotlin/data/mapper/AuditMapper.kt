package data.mapper

import data.dto.AuditDto
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.utils.toSafeUUID


fun AuditDto.mapToAuditEntity(): Audit? {
    return Audit(
        id.toSafeUUID(),
        entityType = getAuditType(entityType),
        entityTypeId = entityTypeId.toSafeUUID(),
        description = description,
        userId = userId.toSafeUUID(),
        dateTime = DateHandlerImp().getLocalDateTimeFromString(dateTime)
    )
}


fun Audit.mapToAuditModel(): AuditDto =
    AuditDto(
        id.toString(),
        entityType = entityType.toString(),
        entityTypeId = entityTypeId.toString(),
        description = description,
        userId = userId.toString(),
        dateTime = dateTime.toString()
    )


fun getAuditType(auditSystem: String): EntityType = when {
    auditSystem.equals("TASK", ignoreCase = true) -> EntityType.TASK
    auditSystem.equals("PROJECT", ignoreCase = true) -> EntityType.PROJECT
    else -> throw Exception("Unknown AuditSystemType: $auditSystem")
}
