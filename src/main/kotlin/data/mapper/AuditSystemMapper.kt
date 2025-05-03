package data.mapper

import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.data.models.AuditSystemModel
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.extention.toSafeUUID


fun AuditSystemModel.mapToAuditSystemEntity(): AuditSystem {
    return AuditSystem(
        id.toSafeUUID(),
        entityType = getAuditSystemType(auditSystemType),
        entityTypeId = entityTypeId.toSafeUUID(),
        description = description,
        userId = userId.toSafeUUID(),
        dateTime = DateHandlerImp().getLocalDateTimeFromString(dateTime)
    )
}

fun AuditSystem.mapToAuditSystemModel(): AuditSystemModel {
    return AuditSystemModel(
        id.toString(),
        auditSystemType = entityTypeId.toString(),
        entityTypeId = entityTypeId.toString(),
        description = description,
        userId = userId.toString(),
        dateTime = dateTime.toString()
    )
}

fun getAuditSystemType(auditSystem: String): EntityType = when {
    auditSystem.equals("AuditSystemType.TASK", ignoreCase = true) -> EntityType.TASK
    auditSystem.equals("AuditSystemType.PROJECT", ignoreCase = true) -> EntityType.PROJECT
    else -> throw Exception("Unknown AuditSystemType: $auditSystem")
}
