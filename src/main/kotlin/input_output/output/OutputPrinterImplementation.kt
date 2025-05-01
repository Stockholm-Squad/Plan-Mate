package org.example.input_output.output

import logic.model.entities.AuditSystem


class OutputPrinterImplementation : OutputPrinter {
    override fun showMessage(message: String) {
        println(message)

    }

    override fun showAudits(audits: List<AuditSystem>) {
        if (audits.isEmpty()) {
            println("No audits to display.")
            return
        }

        val groupedByEntityType = audits.groupBy { it.auditSystemType }

        for ((entityType, entries) in groupedByEntityType) {
            println("===== $entityType =====") // Swimlane title
            for (entry in entries) {
                println("ID: ${entry.id}, EntityID: ${entry.entityId}, ChangedBy: ${entry.changedBy}, Date: ${entry.dateTime}, Action: ${entry.changeDescription}")
            }
            println() // Space between swimlanes
        }
    }


}

