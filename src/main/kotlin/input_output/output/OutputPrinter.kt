package org.example.input_output.output

import logic.model.entities.AuditSystem
import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import logic.model.entities.State


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(tasks: List<MateTaskAssignment>)
    fun showStates(states: List<State>)
    fun showAudits(audits: List<AuditSystem>)
}