package org.example.data.repo

import logic.model.entities.User
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.mapper.UserMapper
import org.example.logic.repository.UserRepository

class UserRepositoryImp(
    private val userCsvDataSource: IUserDataSource,
    private val userMapper: UserMapper,
) : UserRepository {
    override fun createUser(user: User): Result<Boolean> {
        return userCsvDataSource.append(listOf(userMapper.mapToUserModel(user)))
    }

    override fun getAllUsers(): Result<List<User>> {
        return userCsvDataSource.read().fold(
            onSuccess = { userModels -> Result.success(userModels.map { userMapper.mapToUserEntity(it) }) },
            onFailure = { Result.failure(it) })
    }
}