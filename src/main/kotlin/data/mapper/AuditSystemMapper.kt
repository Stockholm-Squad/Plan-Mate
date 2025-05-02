package data.mapper

import logic.model.entities.AuditSystemType
import org.example.data.extention.toLocalDateTime
import org.example.data.extention.toSafeUUID
import org.example.data.models.AuditSystem
class AuditSystemMapper {

fun mapToAuditSystemEntity(auditSystem:AuditSystem):logic.model.entities.AuditSystem{
    return logic.model.entities.AuditSystem(
        auditSystem.id.toSafeUUID(),
        auditSystemType = getAuditSystemType(auditSystem.auditSystemType),
        entityTypeId = auditSystem.entityTypeId.toSafeUUID(),
        description = auditSystem.description,
        userId = auditSystem.userId.toSafeUUID(),
        dateTime = auditSystem.dateTime.toLocalDateTime()
    )
}

fun getAuditSystemType(auditSystem: String): AuditSystemType = when {
    auditSystem.equals("AuditSystemType.TASK", ignoreCase = true) -> AuditSystemType.TASK
    auditSystem.equals("AuditSystemType.PROJECT", ignoreCase = true) -> AuditSystemType.PROJECT
    else -> throw Exception("Unknown AuditSystemType: $auditSystem")
}
}