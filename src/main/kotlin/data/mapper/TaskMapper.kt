package org.example.data.mapper

import logic.model.entities.Task
import org.example.data.extention.toLocalDateTime
import org.example.data.models.TaskModel
import org.example.logic.usecase.extention.toSafeUUID

fun TaskModel.mapToTaskEntity(): Task = Task(
    id.toSafeUUID(),
    name,
    description,
    stateId.toSafeUUID(),
    createdDate.toLocalDateTime(),
    updatedDate.toLocalDateTime()
)

fun Task.mapToTaskModel(): TaskModel = TaskModel(
    id.toString(),
    name,
    description,
    stateId.toString(),
    createdDate.toString(),
    updatedDate.toString(),
)

