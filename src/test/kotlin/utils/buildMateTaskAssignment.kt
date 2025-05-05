package utils

import data.models.MateTaskAssignmentModel

fun buildMateTaskAssignment(
    userName: String = "",
    taskId: String = ""

): MateTaskAssignmentModel {
    return MateTaskAssignmentModel(
        userName, taskId
    )
}
