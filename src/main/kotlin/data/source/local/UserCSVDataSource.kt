package org.example.data.source.local

import data.dto.MateTaskAssignmentDto
import data.dto.UserDto
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.UserDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class UserCSVDataSource(
    private val userReaderWriter: IReaderWriter<UserDto>,
    private val mateTaskAssignmentReaderWriter: IReaderWriter<MateTaskAssignmentDto>,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,

) : UserDataSource {
    override suspend fun addUser(user: UserDto): Boolean = userReaderWriter.append(listOf(user))

    override suspend fun getAllUsers(): List<UserDto> = userReaderWriter.read()

    override suspend fun getUsersByProjectId(projectId: String): List<UserDto> =
        userAssignedToProjectDataSource.getUsersAssignedToProjectByProjectId(projectId)
            .map { userAssignedToProject -> userAssignedToProject.username }
            .let { userIds -> getAllUsers().filter { user -> user.id in userIds } }

    override suspend fun isUserExist(username: String): Boolean =
        getAllUsers().find { user -> user.username == username } != null

    override suspend fun getUserById(userId: String): UserDto? =
        getAllUsers().find { user -> user.id == userId }

    override suspend fun updateUser(user: UserDto): Boolean =
        getAllUsers().map { mappedUser -> if (mappedUser.id == user.id) user else mappedUser }
            .let { updatedUsers -> userReaderWriter.overWrite(updatedUsers) }

    override suspend fun deleteUser(user: UserDto): Boolean =
        getAllUsers().filterNot { mappedUser -> mappedUser.id == user.id }
            .let { updatedUsers -> userReaderWriter.overWrite(updatedUsers) }

    override suspend fun deleteUserFromProject(projectId: String, username: String): Boolean =
        userAssignedToProjectDataSource.deleteUserFromProject(userName = username, projectId = projectId)

    override suspend fun addUserToProject(projectId: String, username: String): Boolean =
        userAssignedToProjectDataSource.addUserToProject(userName = username, projectId = projectId)

    override suspend fun addUserToTask(username: String, taskId: String): Boolean =
        mateTaskAssignmentReaderWriter.append(listOf(MateTaskAssignmentDto(username, taskId)))

    override suspend fun deleteUserFromTask(username: String, taskId: String): Boolean =
        mateTaskAssignmentReaderWriter.read().filterNot { mateTaskAssignment -> mateTaskAssignment.taskId == taskId }
            .let { filteredMateTaskAssignment -> mateTaskAssignmentReaderWriter.overWrite(filteredMateTaskAssignment) }
}