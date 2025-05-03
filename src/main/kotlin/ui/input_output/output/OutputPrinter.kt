package org.example.ui.input_output.output

import logic.model.entities.AuditSystem
import logic.model.entities.Task
import data.models.MateTaskAssignment
import logic.model.entities.ProjectState


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<MateTaskAssignment>)
    fun showStates(projectStates: List<ProjectState>)
    fun showAudits(audits: List<AuditSystem>)
}