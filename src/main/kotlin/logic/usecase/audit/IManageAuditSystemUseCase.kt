package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import java.util.UUID

interface IManageAuditSystemUseCase {
    fun getProjectAuditsByName(projectName: String): Result<List<AuditSystem>>
    fun getTaskAuditsByName(taskName: String): Result<List<AuditSystem>>
    fun getAuditsByUserId(userId: UUID): Result<List<AuditSystem>>
}
