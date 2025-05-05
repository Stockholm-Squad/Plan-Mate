package org.example.logic.usecase.project

import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import logic.model.entities.Project
import org.example.data.utils.DateHandlerImp
import org.example.logic.model.exceptions.ProjectNotFoundException
import org.example.logic.model.exceptions.StateNotExistException
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.audit.AddAuditSystemUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import java.util.*

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageStatesUseCase,
    private val auditSystemRepository: AddAuditSystemUseCase,
) {

    fun getAllProjects(): Result<List<Project>> {
        return projectRepository.getAllProjects()
    }

    fun getProjectByName(projectName: String): Result<Project> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(it) },
            onSuccess = { allProjects -> getProjectFromList(projectName, allProjects) }
        )
    }

    private fun getProjectFromList(projectName: String, allProjects: List<Project>): Result<Project> {
        return allProjects.find { project ->
            project.name == projectName
        }?.let { foundProject ->
            Result.success(foundProject)
        } ?: Result.failure(ProjectNotFoundException())
    }

    fun addProject(projectName: String, stateName: String, userId: UUID): Result<Boolean> {
        val projectStateId = manageProjectStateUseCase.getProjectStateIdByName(stateName)
            ?: return Result.failure(StateNotExistException())
        val newProject = Project(id = UUID.randomUUID(), projectName, projectStateId)

        return projectRepository.addProject(newProject)
            .fold(
                onSuccess = {
                    logAudit(newProject, userId)
                    Result.success(it)
                },
                onFailure = { Result.failure(it) }
            )
    }

    fun updateProject(
        projectId: UUID,
        newProjectName: String,
        newProjectStateName: String,
        userId: UUID
    ): Result<Boolean> {
        val newProjectStateId = manageProjectStateUseCase.getProjectStateIdByName(newProjectStateName)
            ?: return Result.failure(StateNotExistException())
        val updatedProject = Project(id = projectId, name = newProjectName, stateId = newProjectStateId)
        return projectRepository.editProjectState(updatedProject)
            .fold(
                onSuccess = {
                    logAudit(updatedProject, userId)
                    Result.success(it)
                }, onFailure = { Result.failure(it) }
            )
    }

    fun removeProjectByName(projectName: String): Result<Boolean> {
        return getProjectByName(projectName).fold(
            onFailure = { Result.failure(it) },
            onSuccess = { project -> projectRepository.deleteProject(project) }
        )
    }

    fun isProjectExists(projectName: String): Result<Boolean> {
        return getProjectByName(projectName).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun logAudit(updatedProject: Project, userId: UUID) {
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