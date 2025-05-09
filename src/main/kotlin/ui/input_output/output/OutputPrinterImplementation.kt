package org.example.ui.input_output.output

import org.example.logic.entities.AuditSystem
import org.example.logic.entities.ProjectState
import org.example.logic.entities.Task

class OutputPrinterImplementation : OutputPrinter {

    override fun showMessage(message: String) {
        println(message)
    }

    override fun printTask(task: Task) {
        println(
            "Name: ${task.name} | Description: ${task.description} " +
//                "| State: ${task.stateId} " + //TODO: State Name not id
                    "| Created: ${task.createdDate.date} ${task.createdDate.time} | Updated: ${task.updatedDate.date} ${task.updatedDate.time}"
        )
    }

    override fun printTaskList(tasks: List<Task>) {
        tasks.forEach { task -> printTask(task) }
    }

    override fun printMateTaskAssignments(assignments: List<Task>) {
        if (assignments.isEmpty()) return println("No tasks assigned.")

        val userName = assignments.first().name
        println("Tasks assigned to: $userName")
        assignments.forEachIndexed { index, it ->
            println("${index + 1}. Task ID: ${it.id}")
        }
    }

    override fun showStates(projectStates: List<ProjectState>) {
        this.printStateUsingSwimlaneUi(projectStates)
    }

    private fun printStateUsingSwimlaneUi(projectStates: List<ProjectState>) {
        println("┌──────────────────────────────┐")
        println("│ State                        │")
        println("├──────────────────────────────┤")
        projectStates.forEach {
            println("│ %-28s │".format(it.name))
        }
        println("└──────────────────────────────┘")
    }

    override fun showAudits(audits: List<AuditSystem>, username : String) {
        if (audits.isEmpty()) {
            println("No audits to display.")
            return
        }

        val groupedByEntityType = audits.groupBy { it.entityType }

        for ((entityType, entries) in groupedByEntityType) {
            println("\n========== $entityType ==========\n")
            println(
                String.format(
                    "| %-15s | %-20s | %-30s |",
                    "Changed By", "Date", "Change Description"
                )
            )
            println("-".repeat(130))

            for (entry in entries) {
                println(
                    String.format(
                        "| %-15s | %-20s | %-30s |",
                        username,
                        entry.dateTime,
                        entry.description.take(30)
                    )
                )
            }

            println("-".repeat(130)) // Swimlane separator
        }
    }
}
