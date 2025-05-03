package org.example.data.mapper

import logic.model.entities.Task
import org.example.data.models.TaskModel
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.extention.toSafeUUID

fun TaskModel.mapToTaskEntity(): Task = Task(
    id.toSafeUUID(),
    name,
    description,
    stateId.toSafeUUID(),
    DateHandlerImp().getLocalDateTimeFromString(createdDate),
    DateHandlerImp().getLocalDateTimeFromString(updatedDate)
)

fun Task.mapToTaskModel(): TaskModel = TaskModel(
    id.toString(),
    name,
    description,
    stateId.toString(),
    createdDate.toString(),
    updatedDate.toString(),
)

