package org.example.logic.usecase.project

import logic.models.entities.Project
import logic.models.entities.AuditSystem
import logic.models.entities.EntityType
import logic.models.exceptions.ProjectExceptions
import logic.models.exceptions.StateExceptions
import org.example.data.utils.DateHandlerImp
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.audit.AddAuditSystemUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import java.util.*

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageStatesUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val auditSystemRepository: AddAuditSystemUseCase,
) {

    suspend fun addProject(projectName: String, stateName: String, userId: UUID): Boolean {
        return isProjectExists(projectName).let { success ->
            if (!success) {
                val projectStateId = manageProjectStateUseCase.getProjectStateIdByName(stateName)
                val newProject = Project(id = UUID.randomUUID(), projectName, projectStateId)

                projectRepository.addProject(newProject).also {
                    logAudit(newProject, userId)
                }
            } else {
                throw ProjectExceptions.ProjectAlreadyExist()
            }

        }
    }

    suspend fun updateProject(
        projectId: UUID,
        newProjectName: String,
        newProjectStateName: String,
        userId: UUID
    ): Boolean {
        return isProjectExists(newProjectName).let { success ->
            if (success) {
                val newProjectStateId = manageProjectStateUseCase.getProjectStateIdByName(newProjectStateName)
                val updatedProject = Project(id = projectId, name = newProjectName, stateId = newProjectStateId)
                projectRepository.editProject(updatedProject).also {
                    logAudit(updatedProject, userId)
                }
            } else
                throw ProjectExceptions.ProjectNotFoundException()
        }
    }

    suspend fun removeProjectByName(projectName: String): Boolean {
        return getProjectsUseCase.getProjectByName(projectName).let {
            projectRepository.deleteProject(it)
        }
    }

    suspend fun isProjectExists(projectName: String): Boolean {
        return try {
            getProjectsUseCase.getProjectByName(projectName)
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun logAudit(updatedProject: Project, userId: UUID) {
        val auditEntry = AuditSystem(
            entityType = EntityType.PROJECT,
            description = "update project ${updatedProject.name}",
            userId = userId,
            dateTime = DateHandlerImp().getCurrentDateTime(),
            entityTypeId = updatedProject.id
        )
        auditSystemRepository.addAuditsEntries(listOf(auditEntry))
    }
}