package org.example.data.datasources

import org.example.data.entities.UserAssignedToProject

class UserAssignedToProjectCsvDataSource : PlanMateDataSource<UserAssignedToProject> {
    override fun read(): Result<List<UserAssignedToProject>> {
        TODO("Not yet implemented")
    }

    override fun append(model: List<UserAssignedToProject>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun overWrite(model: List<UserAssignedToProject>): Result<Boolean> {
        TODO("Not yet implemented")
    }
}