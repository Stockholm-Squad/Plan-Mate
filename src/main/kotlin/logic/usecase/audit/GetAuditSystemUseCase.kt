package org.example.logic.usecase.audit

import logic.models.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import java.util.*

class GetAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val manageTasksUseCase: ManageTasksUseCase
) {

    suspend fun getProjectAuditsByName(projectName: String): List<AuditSystem> =
        auditSystemRepository.getAllAuditEntries().also { audits ->
            getProjectsUseCase.getProjectByName(projectName).also { project ->
                return audits.filter { audit ->
                    audit.entityTypeId == project.id
                }
            }
        }


    suspend fun getTaskAuditsByName(taskName: String): List<AuditSystem> =
        auditSystemRepository.getAllAuditEntries().also { audits ->
            manageTasksUseCase.getTaskIdByName(taskName).also { taskId ->
                return audits.filter { audit ->
                    audit.entityTypeId == taskId
                }
            }
        }




    suspend fun getAuditsByUserId(userId: UUID): List<AuditSystem> =
        auditSystemRepository.getAllAuditEntries().also { audits ->
            return audits.filter { it.userId == userId }
        }
    


}
