package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import java.util.*

class GetAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageTasksUseCase: ManageTasksUseCase
) : IManageAuditSystemUseCase {

    override fun getProjectAuditsByName(projectName: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = { audits ->
                manageProjectUseCase.getProjectByName(projectName).fold(
                    onSuccess = { project ->
                        val result = audits.filter { audit ->
                            audit.entityTypeId == project.id
                        }
                        Result.success(result)
                    },
                    onFailure = { Result.failure( it )}
                )
            },
            onFailure = { Result.failure(it) }
        )

    override fun getTaskAuditsByName(taskName: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = { audits ->
                manageTasksUseCase.getTaskIdByName(taskName).fold(
                    onSuccess = { taskId ->
                        val result = audits.filter { audit ->
                            audit.entityTypeId == taskId
                        }
                        Result.success(result)
                    },
                    onFailure = {Result.failure(it)}
                )
            },
            onFailure = {Result.failure(it)}
        )



    override fun getAuditsByUserId(userId: UUID): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.userId == userId }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )


}
