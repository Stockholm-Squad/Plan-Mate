package org.example.data.repo

import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: PlanMateDataSource<Project>
) : ProjectRepository {


    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.append(listOf(project)).fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) },
            onSuccess = { Result.success(true) }
        )
    }

    override fun editProject(project: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = {
                it.map {
                    if (it.id == project.id)
                    {
                        project
                    }
                    else
                        it
                }
                projectDataSource.overWrite(it)
            }
        )
    }


    override fun deleteProject(project: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = {
                it.filterNot { it.id == project.id }.let { projectList -> projectDataSource.overWrite(projectList) }

            }
        )
    }


    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read()
    }
}
