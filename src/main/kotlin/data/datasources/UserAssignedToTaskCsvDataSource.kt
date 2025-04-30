package org.example.data.datasources

import org.example.data.entities.UserAssignedToTask

class UserAssignedToTaskCsvDataSource : PlanMateDataSource<UserAssignedToTask> {
    override fun read(): Result<List<UserAssignedToTask>> {
        TODO("Not yet implemented")
    }

    override fun append(model: List<UserAssignedToTask>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<UserAssignedToTask>): Result<Boolean> {
        TODO("Not yet implemented")
    }
}