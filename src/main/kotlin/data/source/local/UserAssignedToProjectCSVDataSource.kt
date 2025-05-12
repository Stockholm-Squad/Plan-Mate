package org.example.data.source.local

import data.dto.UserAssignedToProjectDto
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class UserAssignedToProjectCSVDataSource(
    private val userAssignedToProjectReaderWriter: IReaderWriter<UserAssignedToProjectDto>,
) : UserAssignedToProjectDataSource {
    override suspend fun addUserToProject(projectId: String, userName: String): Boolean =
        userAssignedToProjectReaderWriter.append(
            listOf(UserAssignedToProjectDto(projectId = projectId, userName = userName))
        )

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean =
        userAssignedToProjectReaderWriter.read().filterNot { userAssignedToProject ->
            userAssignedToProject.projectId == projectId && userAssignedToProject.userName == userName
        }.let { updatedUserAssignedToProjectList ->
            userAssignedToProjectReaderWriter.overWrite(updatedUserAssignedToProjectList)
        }

    override suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectReaderWriter.read().filter { userAssignedToProject ->
            userAssignedToProject.projectId == projectId
        }

    override suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectReaderWriter.read().filter { userAssignedToProject ->
            userAssignedToProject.projectId == userName
        }
}