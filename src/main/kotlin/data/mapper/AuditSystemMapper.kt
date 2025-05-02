package data.mapper

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType
import org.example.data.extention.toLocalDateTime
import org.example.data.extention.toSafeUUID
import org.example.data.models.AuditSystemModel

class AuditSystemMapper {

    fun mapToAuditSystemEntity(auditSystemModel: AuditSystemModel): AuditSystem {
        return AuditSystem(
            auditSystemModel.id.toSafeUUID(),
            auditSystemType = getAuditSystemType(auditSystemModel.auditSystemType),
            entityTypeId = auditSystemModel.entityTypeId.toSafeUUID(),
            description = auditSystemModel.description,
            userId = auditSystemModel.userId.toSafeUUID(),
            dateTime = auditSystemModel.dateTime.toLocalDateTime()
        )
    }

    fun mapToAuditSystemModel(auditSystem: AuditSystem): AuditSystemModel  {
        return AuditSystemModel(
            auditSystem.id.toString(),
            auditSystemType = auditSystem.auditSystemType.toString(),
            entityTypeId = auditSystem.entityTypeId.toString(),
            description = auditSystem.description,
            userId = auditSystem.userId.toString(),
            dateTime = auditSystem.dateTime.toString()
        )
    }

    private fun getAuditSystemType(auditSystem: String): AuditSystemType = when {
        auditSystem.equals("AuditSystemType.TASK", ignoreCase = true) -> AuditSystemType.TASK
        auditSystem.equals("AuditSystemType.PROJECT", ignoreCase = true) -> AuditSystemType.PROJECT
        else -> throw Exception("Unknown AuditSystemType: $auditSystem")
    }
}