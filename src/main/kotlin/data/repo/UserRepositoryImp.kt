package org.example.data.repo

import logic.model.entities.User
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.repository.UserRepository

class UserRepositoryImp(
    private val authenticationDataSource: PlanMateDataSource<User>
) : UserRepository {

    override fun getUserByUserName(userName: String): Result<User> {
        TODO("Not yet implemented")
    }
    override fun addUser(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }


}