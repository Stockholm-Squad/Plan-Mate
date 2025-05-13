package org.example.ui.input_output.output

import org.example.logic.entities.Audit
import org.example.logic.entities.EntityState
import org.example.logic.entities.Task


interface OutputPrinter {
    fun showMessageLine(message: String)
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<Task>)
    fun showStates(projectStates: List<EntityState>)
    fun showAudits(audits: List<Audit>, username: String)
}