package org.example.data.datasources

import org.example.data.entities.UserAssignedToProject

class UserAssignedToProjectCsvDataSource : PlanMateDataSource<UserAssignedToProject> {
    override fun read(filePath: String): Result<List<UserAssignedToProject>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<UserAssignedToProject>): Result<Boolean> {
        TODO("Not yet implemented")
    }
}