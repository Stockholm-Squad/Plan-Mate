package utils

import data.dto.MateTaskAssignmentDto

fun buildMateTaskAssignment(
    userName: String = "",
    taskId: String = ""

): MateTaskAssignmentDto {
    return MateTaskAssignmentDto(
        userName, taskId
    )
}
