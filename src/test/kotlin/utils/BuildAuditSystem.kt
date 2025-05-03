package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.data.models.AuditSystemModel
import java.util.UUID


fun createAuditSystemModel(
    id: String,
    entityType: EntityType,
    entityId: String,
    changeDescription: String,
    userId: String
): AuditSystemModel {
    return AuditSystemModel(
        id = id,
        entityType = entityType.toString(),
        entityTypeId = entityId,
        description = changeDescription,
        dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
        userId = userId
    )

}
    fun createAuditSystemEntity(
        entityType: EntityType,
        changeDescription: String,
    ): AuditSystem {
        return AuditSystem(
            entityType = entityType,
            entityTypeId = UUID.randomUUID(),
            description = changeDescription,
            dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            userId =UUID.randomUUID(),
        )}