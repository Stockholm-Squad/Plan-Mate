package org.example.data.datasources

import org.example.data.entities.TaskInProject

class TaskInProjectCsvDataSource : PlanMateDataSource<TaskInProject> {
    override fun read(filePath: String): Result<List<TaskInProject>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<TaskInProject>): Result<Boolean> {
        TODO("Not yet implemented")
    }
}