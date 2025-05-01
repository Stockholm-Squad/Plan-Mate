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
            entityId = "65",
            changeDescription = "waggw",
            changedBy = "mano",
            dateTime = "15/12/2005"
        )
    )

    val result = data.recordAuditsEntries(data1)
    println(result.isSuccess)
}