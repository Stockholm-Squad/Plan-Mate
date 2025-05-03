package org.example.data.repo

import logic.model.entities.User
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.mapper.UserMapper
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository
import java.util.*

class UserRepositoryImp(
    private val userCsvDataSource: IUserDataSource,
    private val userMapper: UserMapper,
) : UserRepository {
    override fun addUser(user: User): Result<Boolean> {
        return userCsvDataSource.append(listOf(userMapper.mapToUserModel(user)))
    }

    override fun getAllUsers(): Result<List<User>> {
        return userCsvDataSource.read().fold(
            onSuccess = { userModels -> Result.success(userModels.map { userMapper.mapToUserEntity(it) }) },
            onFailure = { Result.failure(it) })
    }

    override fun getUsersByProjectId(projectId: UUID): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun addUserToProject(projectId: UUID, userName: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteUserFromProject(projectId: UUID, userName: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllUsersByTaskId(taskId: UUID): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun addUserToTask(mateName: String, taskId: UUID): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteUserFromTask(mateName: String, taskId: UUID): Result<Boolean> {
        TODO("Not yet implemented")
    }

}