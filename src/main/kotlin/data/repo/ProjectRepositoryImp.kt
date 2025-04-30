package org.example.data.repo

import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: PlanMateDataSource<Project>
) : ProjectRepository {

    companion object {
        private var allProjects: MutableList<Project> = emptyList<Project>().toMutableList()
    }

    override fun addProject(project: Project): Result<Boolean> {
        if (allProjects.size == 0) {
            getAllProjects()
        }
        allProjects.add(project)
        return projectDataSource.write(allProjects).fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) },
            onSuccess = { Result.success(true) }
        )
    }

    override fun editProject(project: Project): Result<Boolean> {
        return projectDataSource.write(listOf(project)).fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = {
                val updated = updateProjectInTheList(project)
                if (updated) {
                    projectDataSource.write(allProjects)
                }
                Result.success(updated)
            }
        )
    }

    private fun updateProjectInTheList(project: Project): Boolean {
        return try {
            val index = allProjects.indexOfFirst { it.id == project.id }
            if (index != -1) {
                allProjects[index] = project
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteProject(project: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = {
                Result.success(allProjects.remove(project)).onSuccess { projectDataSource.write(allProjects) }
            }
        )
    }


    override fun getAllProjects(): Result<List<Project>> {
        return allProjects
            .takeIf { it.isNotEmpty() }?.let { Result.success(it) }
            ?: projectDataSource.read().fold(
                onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) },
                onSuccess = { projects ->
                    Result.success(projects).onSuccess { allProjects = projects.toMutableList() }
                }
            )
    }
}
