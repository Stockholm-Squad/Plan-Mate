package org.example.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Audit(
    val id: UUID = UUID.randomUUID(),
    val entityType: EntityType,
    val entityTypeId: UUID,
    val description: String,
    val userId: UUID,
    val dateTime: LocalDateTime,
)

enum class EntityType {
    TASK, PROJECT, STATE, UNKNOWN;

    companion object {
        fun getAuditType(auditEntityTypeName: String): EntityType {
            return EntityType.entries.find { it.name == auditEntityTypeName } ?: UNKNOWN
        }
    }
}