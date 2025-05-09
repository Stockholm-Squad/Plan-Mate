package org.example.ui.input_output.output

import org.example.logic.entities.AuditSystem
import org.example.logic.entities.ProjectState
import org.example.logic.entities.Task


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<Task>)
    fun showStates(projectStates: List<ProjectState>)
    fun showAudits(audits: List<AuditSystem>, username : String)
}