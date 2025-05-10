package org.example.data.mapper

import org.example.logic.entities.Task
import data.dto.TaskDto
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.extention.toSafeUUID

fun TaskDto.mapToTaskEntity(): Task? {
    return try {
        Task(
            id.toSafeUUID(),
            projectName,
            name,
            description,
            stateId.toSafeUUID(),
            DateHandlerImp().getLocalDateTimeFromString(createdDate),
            DateHandlerImp().getLocalDateTimeFromString(updatedDate)
        )
    } catch (throwable: Throwable) {
        null
    }
}

fun Task.mapToTaskModel(): TaskDto = TaskDto(
    id.toString(),
    projectName,
    name,
    description,
    stateId.toString(),
    createdDate.toString(),
    updatedDate.toString(),
)

