package utils

import data.models.MateTaskAssignment

fun buildMateTaskAssignment(
    userName: String = "",
    taskId: String = ""

): MateTaskAssignment {
    return MateTaskAssignment(
        userName, taskId
    )
}
