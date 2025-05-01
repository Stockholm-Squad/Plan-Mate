package org.example.data.datasources

import logic.model.entities.User

class AuthenticationCsvDataSource : PlanMateDataSource<User> {
    override fun read(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun append(model: List<User>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun overWrite(model: List<User>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}