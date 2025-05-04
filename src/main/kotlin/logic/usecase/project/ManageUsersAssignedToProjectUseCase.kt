package org.example.logic.usecase.project

import logic.model.entities.User
import org.example.logic.repository.UserRepository
import java.util.*

class ManageUsersAssignedToProjectUseCase(
    private val userRepository: UserRepository,
) {

    fun getUsersByProjectId(projectId: UUID): Result<List<User>> {
        return userRepository.getUsersByProjectId(projectId = projectId)
    }


    fun addUserToProject(projectId: String, userName: String): Result<Boolean> {
//        return userRepository.addUserToProject(projectId = projectId, userName = userName)
        return Result.success(true)
    }

    fun deleteUserFromProject(projectId: UUID, userName: String): Result<Boolean> {
        return userRepository.deleteUserFromProject(projectId = projectId, userName = userName)
    }

}