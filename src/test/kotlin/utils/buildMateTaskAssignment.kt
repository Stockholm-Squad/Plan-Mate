package utils

import org.example.data.entities.MateTaskAssignment

fun buildMateTaskAssignment(
    userName: String = "",
    taskId: String = ""

): MateTaskAssignment {
    return MateTaskAssignment(
        userName, taskId
    )
}
