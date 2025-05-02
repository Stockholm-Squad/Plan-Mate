package data.mapper

import logic.model.entities.AuditSystemType
import org.example.data.mapper.toLocalDateTime
import org.example.data.mapper.toSafeUUID
import org.example.data.mapper.toUser
import org.example.data.models.AuditSystem

fun AuditSystem.toDomainAuditSystemEntity(): logic.model.entities.AuditSystem {
    return logic.model.entities.AuditSystem(
        id = id.toSafeUUID(),
        auditSystemType = getAuditSystemType( auditSystemType),
        entityId = entityId.toSafeUUID(),
        description = description,
        changedBy = changedBy.toUser(),
        dateTime = dateTime.toLocalDateTime()
    )}

fun getAuditSystemType(auditSystem:String): AuditSystemType = when {
    auditSystem.equals("AuditSystemType.TASK", ignoreCase = true) -> AuditSystemType.TASK
    auditSystem.equals("AuditSystemType.PROJECT", ignoreCase = true) -> AuditSystemType.PROJECT
    else -> throw Exception("Unknown AuditSystemType: $auditSystem")
}