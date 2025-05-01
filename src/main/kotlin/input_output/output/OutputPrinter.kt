package org.example.input_output.output

import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment


interface OutputPrinter {
    fun showMessage(message: String)
    fun printTask(task: Task)
    fun printTaskList(tasks: List<Task>)
    fun printMateTaskAssignments(assignments: List<MateTaskAssignment>)
}