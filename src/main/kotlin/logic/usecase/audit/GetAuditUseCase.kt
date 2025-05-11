package org.example.logic.usecase.audit

import org.example.logic.entities.Audit
import org.example.logic.repository.AuditRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import java.util.*

class GetAuditUseCase(
    private val auditRepository: AuditRepository,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val manageTasksUseCase: ManageTasksUseCase
) {

    suspend fun getAuditsForProjectByName(projectName: String): List<Audit> =
        auditRepository.getAllAudits().also { audits ->
            getProjectsUseCase.getProjectByName(projectName).also { project ->
                return audits.filter { audit ->
                    audit.entityTypeId == project.id
                }
            }
        }

    suspend fun getAuditsForTaskByName(taskName: String): List<Audit> =
        auditRepository.getAllAudits().also { audits ->
            manageTasksUseCase.getTaskIdByName(taskName).also { taskId ->
                return audits.filter { audit ->
                    audit.entityTypeId == taskId
                }
            }
        }

    suspend fun getAuditsForUserById(userId: UUID): List<Audit> =
        auditRepository.getAllAudits().also { audits ->
            return audits.filter { it.userId == userId }
        }

}
