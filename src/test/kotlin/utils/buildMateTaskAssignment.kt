package utils

import data.dto.MateTaskAssignmentModel

fun buildMateTaskAssignment(
    userName: String = "",
    taskId: String = ""

): MateTaskAssignmentModel {
    return MateTaskAssignmentModel(
        userName, taskId
    )
}
