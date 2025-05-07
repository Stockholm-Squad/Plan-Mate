package org.example.logic.usecase.project

import logic.models.entities.User
import org.example.logic.repository.UserRepository
import java.util.*

class ManageUsersAssignedToProjectUseCase(
    private val userRepository: UserRepository,
) {

    suspend fun getUsersByProjectId(projectId: UUID): List<User> {
        return userRepository.getUsersByProjectId(projectId = projectId)
    }


    suspend fun addUserToProject(projectId: UUID, userName: String): Boolean {
        return userRepository.addUserToProject(projectId = projectId, userName = userName)
    }

    suspend fun deleteUserFromProject(projectId: UUID, userName: String): Boolean {
        return userRepository.deleteUserFromProject(projectId = projectId, userName = userName)
    }

}