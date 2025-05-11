package org.example.ui.input_output.output

import org.example.logic.entities.Audit
import org.example.logic.entities.EntityState
import org.example.logic.entities.EntityType
import org.example.logic.entities.Task
import java.util.*


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<Task>)
    fun showStates(projectStates: List<EntityState>)
    fun showAudits(audits: List<Audit>, username: String)

    fun printAddEntityDescription(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String = ""
    ): String

    fun printUpdateEntityDescription(
        entityType: EntityType,
        existEntityName: String,
        newEntityName: String,
        entityId: UUID,
        newDescription: String = "",
        newStateName: String = "",
    ): String

    fun printDeleteEntityDescription(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String = ""
    ): String
}