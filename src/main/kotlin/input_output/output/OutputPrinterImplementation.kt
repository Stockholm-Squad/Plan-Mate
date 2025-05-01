package org.example.input_output.output

import logic.model.entities.State
import logic.model.entities.AuditSystem

class OutputPrinterImplementation : OutputPrinter {
    override fun showMessage(message: String) {
        println(message)

    }

    override fun showStates(states: List<State>) {
        this.printStateUsingSwimlaneUi(states)
    }

    private fun printStateUsingSwimlaneUi(states: List<State>) {
        println("┌──────────────────────────────┐")
        println("│ State                        │")
        println("├──────────────────────────────┤")
        states.forEach {
            println("│ %-28s │".format(it.name))
        }
        println("└──────────────────────────────┘")
    }

    override fun showAudits(audits: List<AuditSystem>) {
        if (audits.isEmpty()) {
            println("No audits to display.")
            return
        }

        val groupedByEntityType = audits.groupBy { it.auditSystemType }

        for ((entityType, entries) in groupedByEntityType) {
            println("\n========== $entityType ==========\n")
            println(
                String.format(
                    "| %-36s | %-12s | %-15s | %-20s | %-30s |",
                    "ID", "Entity ID", "Changed By", "Date", "Change Description"
                )
            )
            println("-".repeat(130))

            for (entry in entries) {
                println(
                    String.format(
                        "| %-36s | %-12s | %-15s | %-20s | %-30s |",
                        entry.id,
                        entry.entityId,
                        entry.changedBy,
                        entry.dateTime,
                        entry.changeDescription.take(30)
                    )
                )
            }

            println("-".repeat(130)) // Swimlane separator
        }
    }
}
