package data.mapper

import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import data.dto.AuditSystemDto
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.extention.toSafeUUID


fun AuditSystemDto.mapToAuditSystemEntity(): AuditSystem? {
    return try {
        AuditSystem(
            id.toSafeUUID(),
            entityType = getAuditSystemType(entityType),
            entityTypeId = entityTypeId.toSafeUUID(),
            description = description,
            userId = userId.toSafeUUID(),
            dateTime = DateHandlerImp().getLocalDateTimeFromString(dateTime)
        )
    } catch (throwable: Throwable) {
        null
    }
}


fun AuditSystem.mapToAuditSystemModel(): AuditSystemDto {
    return AuditSystemDto(
        id.toString(),
        entityType = entityType.toString(),
        entityTypeId = entityTypeId.toString(),
        description = description,
        userId = userId.toString(),
        dateTime = dateTime.toString()
    )
}

fun getAuditSystemType(auditSystem: String): EntityType = when {
    auditSystem.equals("TASK", ignoreCase = true) -> EntityType.TASK
    auditSystem.equals("PROJECT", ignoreCase = true) -> EntityType.PROJECT
    else -> throw Exception("Unknown AuditSystemType: $auditSystem")
}
