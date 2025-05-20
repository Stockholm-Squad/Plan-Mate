package org.example.logic.usecase.audit.utils

import logic.usecase.audit.utils.*
import org.example.logic.entities.EntityType
import java.util.*

class AuditDescriptionProvider {

    fun getAuditDescriptionForAdd(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String
    ): String {
        return when (entityType) {
            EntityType.TASK -> TASK_MESSAGE_TEMPLATE.format(
                entityType, ADDED_ACTION, entityName, entityId, TASK_PROJECT_SUFFIX.format(additionalInfo)
            )

            EntityType.PROJECT -> PROJECT_MESSAGE_TEMPLATE.format(
                entityType, ADDED_ACTION, entityName, entityId, PROJECT_STATE_SUFFIX.format(additionalInfo)
            )

            EntityType.STATE -> STATE_MESSAGE_TEMPLATE.format(entityType, ADDED_ACTION, entityName, entityId)
            EntityType.UNKNOWN -> EntityType.UNKNOWN.name
        }
    }

    fun getAuditDescriptionForUpdate(
        entityType: EntityType,
        existEntityName: String,
        newEntityName: String,
        entityId: UUID,
        newDescription: String,
        newStateName: String,
        additionalInfo: String
    ): String {
        return when (entityType) {
            EntityType.TASK -> TASK_MESSAGE_TEMPLATE.format(
                entityType, UPDATED_ACTION,
                NAME_UPDATE.format(existEntityName, newEntityName), entityId,
                COMBINED_DETAIL_FORMAT.format(
                    DESCRIPTION_UPDATE.format(newDescription.take(30)),
                    STATE_UPDATED.format(newStateName), TASK_PROJECT_SUFFIX.format(additionalInfo)
                )
            )

            EntityType.PROJECT -> PROJECT_MESSAGE_TEMPLATE.format(
                entityType, UPDATED_ACTION, NAME_UPDATE.format(existEntityName, newEntityName), entityId,
                STATE_UPDATED.format(newStateName)
            )

            EntityType.STATE -> STATE_MESSAGE_TEMPLATE.format(
                entityType, UPDATED_ACTION, NAME_UPDATE.format(existEntityName, newEntityName), entityId
            )

            EntityType.UNKNOWN -> EntityType.UNKNOWN.name
        }
    }

    fun getAuditDescriptionForDelete(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String
    ): String {
        return when (entityType) {
            EntityType.TASK -> TASK_MESSAGE_TEMPLATE.format(
                entityType, DELETED_ACTION, entityName, entityId, TASK_PROJECT_SUFFIX.format(additionalInfo)
            )

            EntityType.PROJECT -> PROJECT_MESSAGE_TEMPLATE.format(
                entityType, DELETED_ACTION, entityName, entityId, PROJECT_STATE_SUFFIX.format(additionalInfo)
            )

            EntityType.STATE -> STATE_MESSAGE_TEMPLATE.format(entityType, DELETED_ACTION, entityName, entityId)
            EntityType.UNKNOWN -> EntityType.UNKNOWN.name
        }
    }
}