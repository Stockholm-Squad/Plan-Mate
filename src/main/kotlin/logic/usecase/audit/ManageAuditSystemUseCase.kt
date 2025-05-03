package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.extention.toSafeUUID
import java.util.*

class ManageAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository
) : IManageAuditSystemUseCase {

    override fun getAuditsByEntityTypeId(entityId: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                try {
                    val result = it.filter { it.entityTypeId == entityId.toSafeUUID() }
                    Result.success(result)
                }catch (e: Exception){
                    return Result.failure(e)
                }
            },
            onFailure = { Result.failure(it) }
        )

    override fun getAuditsByUserId(userId: UUID): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.userId == userId }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    override fun addAuditsEntries(auditEntry: List<AuditSystem>): Result<Boolean> =
        auditSystemRepository.addAuditsEntries(auditEntry)

}
