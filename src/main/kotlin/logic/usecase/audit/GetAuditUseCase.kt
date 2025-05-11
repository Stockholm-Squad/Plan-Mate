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

    suspend fun getAuditsForTaskByName(taskName: String): List<Audit> {
        val result = mutableListOf<Audit>()
        auditRepository.getAllAudits().also { audits ->
            manageTasksUseCase.getAllTasks().filter { task ->
                task.name == taskName
            }.forEach { task ->
                audits.filter { audit ->
                    audit.entityTypeId == task.id
                }.also { filteredAudits ->
                    result.addAll(filteredAudits)
                }
            }
        }
        return result
    }

    suspend fun getAuditsForUserById(userId: UUID): List<Audit> =
        auditRepository.getAllAudits().also { audits ->
            return audits.filter { it.userId == userId }
        }

}
