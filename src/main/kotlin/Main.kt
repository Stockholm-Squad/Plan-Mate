package org.example

import logic.model.entities.AuditSystem
import org.example.data.datasources.AuditSystemCsvDataSource
import org.example.data.repo.AuditSystemRepositoryImp

fun main() {
    val data = AuditSystemRepositoryImp(AuditSystemCsvDataSource("audits.csv"))
//    data.getTaskChangeLogsById(3).fold(
//        onSuccess = { println(it) },
//        onFailure = { println(it.message) }
//    )
    val data1 = listOf(
        AuditSystem(
            auditSystemType = "TASK",
            entityId = "3",
            changeDescription = "SAFAFGA",
            changedBy = "mano",
            dateTime = "15/12/2005"
        )
    )

    data.recordAuditsEntries(data1).fold(
        onSuccess = { println(it) },
        onFailure = { println(it.message) }
    )
}