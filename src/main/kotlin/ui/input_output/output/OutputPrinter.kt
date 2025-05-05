package org.example.ui.input_output.output

import logic.models.entities.AuditSystem
import logic.models.entities.ProjectState
import logic.models.entities.Task


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<Task>)
    fun showStates(projectStates: List<ProjectState>)
    fun showAudits(audits: List<AuditSystem>)
}