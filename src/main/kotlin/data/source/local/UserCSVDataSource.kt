package org.example.data.source.local

import data.dto.UserDto
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.UserDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class UserCSVDataSource(
    private val userReaderWriter: IReaderWriter<UserDto>,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
) : UserDataSource {
    override suspend fun addUser(user: UserDto): Boolean =
        userReaderWriter.append(listOf(user))

    override suspend fun getAllUsers(): List<UserDto> =
        userReaderWriter.read()

    override suspend fun getUsersByProjectId(projectId: String): List<UserDto> =
        userAssignedToProjectDataSource.getUsersAssignedToProjectByProjectId(projectId)
            .map { userAssignedToProject -> userAssignedToProject.userName }
            .let { userIds -> getAllUsers().filter { user -> user.id in userIds } }

    override suspend fun isUserExist(username: String): Boolean =
        getAllUsers().find { user -> user.username == username } != null

    override suspend fun getUserById(userId: String): UserDto? =
        getAllUsers().find { user -> user.id == userId }

    override suspend fun editUser(user: UserDto): Boolean =
        getAllUsers().map { mappedUser -> if (mappedUser.id == user.id) user else mappedUser }
            .let { updatedUsers -> userReaderWriter.overWrite(updatedUsers) }

    override suspend fun deleteUser(user: UserDto): Boolean =
        getAllUsers().filterNot { mappedUser -> mappedUser.id == user.id }
            .let { updatedUsers -> userReaderWriter.overWrite(updatedUsers) }
}