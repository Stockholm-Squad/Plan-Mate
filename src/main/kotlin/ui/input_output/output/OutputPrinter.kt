package org.example.ui.input_output.output

import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.entities.ProjectState
import org.example.logic.entities.Task
import java.util.*


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<Task>)
    fun showStates(projectStates: List<ProjectState>)
    fun showAudits(audits: List<Audit>, username : String)
    fun printAddTaskDescription(entityType: EntityType, taskName: String, taskId: UUID, projectName: String): String
    fun printUpdateTaskDescription(
        entityType: EntityType,
        newTaskName: String,
        newDescription: String,
        newStateName: String
    ): String

    fun printDeleteTaskDescription(entityType: EntityType, taskName: String, taskId: UUID, projectName: String): String
}