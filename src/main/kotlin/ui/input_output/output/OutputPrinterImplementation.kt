package org.example.ui.input_output.output

import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import org.example.logic.entities.ProjectState
import org.example.logic.entities.Task
import java.util.*

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

    override fun showAudits(audits: List<AuditSystem>, username: String) {
        if (audits.isEmpty()) {
            println("No audits to display.")
            return
        }

        val groupedByEntityType = audits.groupBy { it.entityType }

        for ((entityType, entries) in groupedByEntityType) {
            println("\n========== $entityType ==========\n")

            println(String.format("| %-15s | %-110s | %-30s |", "Changed By", "Change Description", "Date"))
            println("-".repeat(165))

            for (entry in entries) {
                println(
                    String.format(
                        "| %-15s | %-110s | %-30s |",
                        username,
                        entry.description.take(205),
                        entry.dateTime
                    )
                )
            }

            println("-".repeat(165))
        }
    }

    override fun printAddTaskDescription(
        entityType: EntityType,
        taskName: String,
        taskId: UUID,
        projectName: String
    ): String {
        return "$entityType: added'$taskName' (ID: $taskId) to the '$projectName' project."
    }

    override fun printUpdateTaskDescription(
        entityType: EntityType,
        newTaskName: String,
        newDescription: String,
        newStateName: String
    ): String {
        return "$entityType: " +
                "Updated Task name '$newTaskName'. " +
                "Description '${newDescription.take(30)}'. " +
                "State updated to '$newStateName'."
    }

    override fun printDeleteTaskDescription(
        entityType: EntityType,
        taskName: String,
        taskId: UUID,
        projectName: String
    ): String {
        return "$entityType: Task '$taskName' (ID: $taskId) was deleted from project '$projectName'."
    }

}
