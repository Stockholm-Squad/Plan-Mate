package org.example.data.mapper

import data.dto.TaskDto
import org.example.logic.entities.Task
import org.example.logic.utils.DateHandlerImp
import org.example.logic.utils.toSafeUUID

fun TaskDto.mapToTaskEntity(): Task? {
    return Task(
        id = id.toSafeUUID() ?: return null,
        projectTitle = projectTitle,
        title = title,
        description = description,
        stateId = stateId.toSafeUUID() ?: return null,
        createdDate = DateHandlerImp().getLocalDateTimeFromString(createdDate),
        updatedDate = DateHandlerImp().getLocalDateTimeFromString(updatedDate)
    )
}

fun Task.mapToTaskModel(): TaskDto {
    return TaskDto(
        id = id.toString(),
        projectTitle = projectTitle,
        title = title,
        description = description,
        stateId = stateId.toString(),
        createdDate = createdDate.toString(),
        updatedDate = updatedDate.toString(),
    )
}
