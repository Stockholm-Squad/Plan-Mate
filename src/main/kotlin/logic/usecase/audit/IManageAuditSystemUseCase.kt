package org.example.logic.usecase.audit

import logic.models.entities.AuditSystem
import java.util.*

interface IManageAuditSystemUseCase {
    fun getProjectAuditsByName(projectName: String): Result<List<AuditSystem>>
    fun getTaskAuditsByName(taskName: String): Result<List<AuditSystem>>
    fun getAuditsByUserId(userId: UUID): Result<List<AuditSystem>>
}
