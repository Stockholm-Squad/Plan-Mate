package org.example.data.source.local

import data.dto.ProjectDto
import org.example.data.source.ProjectDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class ProjectCSVDataSource(
    private val projectReaderWriter: IReaderWriter<ProjectDto>,
    private val userAssignedToProjectReaderWriter: UserAssignedToProjectDataSource,
) : ProjectDataSource {
    override suspend fun addProject(project: ProjectDto): Boolean =
        projectReaderWriter.append(listOf(project))

    override suspend fun editProject(updatedProject: ProjectDto): Boolean =
        getAllProjects().map { project -> if (updatedProject.id == project.id) updatedProject else project }
            .let { updatedProjects -> projectReaderWriter.overWrite(updatedProjects) }

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean =
        getAllProjects().filterNot { project -> projectToDelete.id == project.id }
            .let { updatedProjects -> projectReaderWriter.overWrite(updatedProjects) }

    override suspend fun getAllProjects(): List<ProjectDto> =
        projectReaderWriter.read()

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> =
        userAssignedToProjectReaderWriter.getUsersAssignedToProjectByUserName(username)
            .map { it.projectId }
            .let { projectIds -> getAllProjects().filter { it.id in projectIds } }
}