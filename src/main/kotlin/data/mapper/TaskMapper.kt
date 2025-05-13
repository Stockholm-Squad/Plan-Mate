package org.example.data.mapper

import data.dto.TaskDto
import org.example.logic.entities.Task
import org.example.logic.utils.DateHandlerImp
import org.example.logic.utils.toSafeUUID

fun TaskDto.mapToTaskEntity(): Task? {
    return Task(
        id.toSafeUUID() ?: return null,
        projectName,
        title,
        description,
        stateId.toSafeUUID() ?: return null,
        DateHandlerImp().getLocalDateTimeFromString(createdDate),
        DateHandlerImp().getLocalDateTimeFromString(updatedDate)
    )
}

fun Task.mapToTaskModel(): TaskDto {
    return TaskDto(
        id.toString(),
        projectName,
        title,
        description,
        stateId.toString(),
        createdDate.toString(),
        updatedDate.toString(),
    )
}
