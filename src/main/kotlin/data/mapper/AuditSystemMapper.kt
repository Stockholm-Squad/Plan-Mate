package data.mapper

import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.data.extention.toLocalDateTime
import org.example.data.models.AuditSystemModel
import org.example.logic.usecase.extention.toSafeUUID

class AuditSystemMapper {

    fun mapToAuditSystemEntity(auditSystemModel: AuditSystemModel): AuditSystem {
        return AuditSystem(
            auditSystemModel.id.toSafeUUID(),
            entityType = getAuditSystemType(auditSystemModel.auditSystemType),
            entityTypeId = auditSystemModel.entityTypeId.toSafeUUID(),
            description = auditSystemModel.description,
            userId = auditSystemModel.userId.toSafeUUID(),
            dateTime = auditSystemModel.dateTime.toLocalDateTime()
        )
    }

    fun mapToAuditSystemModel(auditSystem: AuditSystem): AuditSystemModel {
        return AuditSystemModel(
            auditSystem.id.toString(),
            auditSystemType = auditSystem.entityType.toString(),
            entityTypeId = auditSystem.entityTypeId.toString(),
            description = auditSystem.description,
            userId = auditSystem.userId.toString(),
            dateTime = auditSystem.dateTime.toString()
        )
    }

    private fun getAuditSystemType(auditSystem: String): EntityType = when {
        auditSystem.equals("AuditSystemType.TASK", ignoreCase = true) -> EntityType.TASK
        auditSystem.equals("AuditSystemType.PROJECT", ignoreCase = true) -> EntityType.PROJECT
        else -> throw Exception("Unknown AuditSystemType: $auditSystem")
    }
}