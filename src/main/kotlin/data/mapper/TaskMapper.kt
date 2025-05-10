package org.example.data.mapper

import data.dto.TaskDto
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Task
import org.example.logic.utils.toSafeUUID

fun TaskDto.mapToTaskEntity(): Task? =
    Task(
        id.toSafeUUID(),
        projectName,
        name,
        description,
        stateId.toSafeUUID(),
        DateHandlerImp().getLocalDateTimeFromString(createdDate),
        DateHandlerImp().getLocalDateTimeFromString(updatedDate)
    )


fun Task.mapToTaskModel(): TaskDto = TaskDto(
    id.toString(),
    projectName,
    name,
    description,
    stateId.toString(),
    createdDate.toString(),
    updatedDate.toString(),
)

