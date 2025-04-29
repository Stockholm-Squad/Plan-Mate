package org.example.data.datasources

import logic.model.entities.User

class AuthenticationCsvDataSource : PlanMateDataSource<User> {
    override fun read(filePath: String): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<User>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}