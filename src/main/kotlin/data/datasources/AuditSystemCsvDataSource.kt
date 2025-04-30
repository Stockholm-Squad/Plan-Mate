package org.example.data.datasources

import logic.model.entities.AuditSystem

class AuditSystemCsvDataSource : PlanMateDataSource<AuditSystem> {
    override fun read(): Result<List<AuditSystem>> {
        TODO("Not yet implemented")
    }

    override fun append(model: List<AuditSystem>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun overWrite(model: List<AuditSystem>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}