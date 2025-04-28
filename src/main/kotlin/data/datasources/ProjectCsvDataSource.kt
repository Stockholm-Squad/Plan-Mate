package org.example.data.datasources

import logic.model.entities.Project

class ProjectCsvDataSource : PlanMateDataSource<Project> {
    override fun read(filePath: String): Result<List<Project>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<Project>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}