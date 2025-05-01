package org.example.data.repo

import logic.model.entities.User
import org.example.data.datasources.PlanMateDataSource
import org.example.data.datasources.user_data_source.UserDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository

class UserRepositoryImp(
    private val userCsvDataSource: UserDataSource
) : UserRepository {
    override fun createUser(user: User): Result<Boolean> {

        return userCsvDataSource.append(listOf(user)).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(exception = it) })
    }

    override fun getAllUsers(): Result<List<User>> {
        return userCsvDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.UsersIsEmpty()) })
    }


}