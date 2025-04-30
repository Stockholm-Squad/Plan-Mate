package org.example.data.repo

import logic.model.entities.User
import org.example.data.datasources.PlanMateDataSource
import org.example.data.datasources.UserCsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository

class UserRepositoryImp(
    private val userCsvDataSource: PlanMateDataSource<User>
) : UserRepository {
    override fun addUser(user: User): Result<Boolean> {

        return userCsvDataSource.write(listOf(user)).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(exception = it) })
    }

    override fun getAllUsers(): Result<List<User>> {
        return userCsvDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.UsersIsEmpty()) })
    }


}