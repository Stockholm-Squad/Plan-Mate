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
        auditRepository.getAllAudits()
            .filter { audit -> audit.entityTypeId == getProjectsUseCase.getProjectByName(projectName).id }

    suspend fun getAuditsForTaskByName(taskName: String): List<Audit> =
        manageTasksUseCase.getAllTasks()
            .filter { task -> task.title == taskName }
            .map { task -> task.id }
            .let { tasksId ->
                auditRepository.getAllAudits()
                    .filter { audit -> audit.entityTypeId in tasksId }
            }


    suspend fun getAuditsForUserById(userId: UUID): List<Audit> =
        auditRepository.getAllAudits().filter { it.userId == userId }

}
