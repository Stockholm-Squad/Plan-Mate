package org.example.data.source.local

import data.dto.ProjectDto
import data.dto.UserAssignedToProjectDto
import org.example.data.source.ProjectDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.example.logic.ProjectNotFoundException

class ProjectCSVDataSource(
    private val projectReaderWriter: IReaderWriter<ProjectDto>,
    private val userAssignedToProjectReaderWriter: IReaderWriter<UserAssignedToProjectDto>,
) : ProjectDataSource {
    override suspend fun addProject(project: ProjectDto): Boolean = projectReaderWriter.append(listOf(project))

    override suspend fun updateProject(projectToUpdate: ProjectDto): Boolean =
        getAllProjects().map { project -> if (projectToUpdate.id == project.id) projectToUpdate else project }
            .let { updatedProjects -> projectReaderWriter.overWrite(updatedProjects) }

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean =
        getAllProjects().filterNot { project -> projectToDelete.id == project.id }
            .let { updatedProjects -> projectReaderWriter.overWrite(updatedProjects) }

    override suspend fun getAllProjects(): List<ProjectDto> =
        projectReaderWriter.read()

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> =
        getUsersAssignedToProjectByUserName(username)
            .map { it.projectId }
            .let { projectIds -> getAllProjects().filter { it.id in projectIds } }

    override suspend fun getProjectByName(projectName: String): ProjectDto {
        return getAllProjects().find { project ->
            project.title == projectName
        } ?: throw ProjectNotFoundException()
    }

    override suspend fun getProjectById(projectId: String): ProjectDto {
        return getAllProjects().find { project ->
            project.id == projectId
        } ?: throw ProjectNotFoundException()
    }

    private suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectReaderWriter.read().filter { userAssignedToProject ->
            userAssignedToProject.projectId == userName
        }
}