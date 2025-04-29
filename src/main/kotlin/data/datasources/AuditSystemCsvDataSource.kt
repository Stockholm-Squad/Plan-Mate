package org.example.data.datasources

import logic.model.entities.AuditSystem

class AuditSystemCsvDataSource(
    private val filePath: String
) : PlanMateDataSource<AuditSystem> {
    override fun read(filePath: String): Result<List<AuditSystem>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<AuditSystem>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}