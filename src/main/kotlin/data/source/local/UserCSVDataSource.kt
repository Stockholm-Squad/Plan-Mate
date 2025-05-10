package org.example.data.source.local

import data.dto.UserDto
import org.example.data.source.UserDataSource

class UserCSVDataSource : UserDataSource {
    override suspend fun addUser(user: UserDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsers(): List<UserDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersByProjectId(projectId: String): List<UserDto> {
        TODO("Not yet implemented")
    }

    override suspend fun isUserExist(username: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): UserDto? {
        TODO("Not yet implemented")
    }

    override suspend fun editUser(user: UserDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: UserDto): Boolean {
        TODO("Not yet implemented")
    }
}