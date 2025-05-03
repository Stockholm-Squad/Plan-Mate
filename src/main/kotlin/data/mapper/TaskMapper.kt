package org.example.data.mapper

import logic.model.entities.Task
import org.example.data.models.TaskModel
import org.example.data.utils.DateHandlerImp
import org.example.logic.usecase.extention.toSafeUUID

class TaskMapper {
    fun mapToTaskEntity(taskModel: TaskModel): Task = Task(
        taskModel.id.toSafeUUID(),
        taskModel.name,
        taskModel.description,
        taskModel.stateId.toSafeUUID(),
        DateHandlerImp().getLocalDateTimeFromString(taskModel.createdDate),
        DateHandlerImp().getLocalDateTimeFromString(taskModel.updatedDate)
    )

    fun mapToTaskModel(task: Task): TaskModel = TaskModel(
        task.id.toString(),
        task.name,
        task.description,
        task.stateId.toString(),
        task.createdDate.toString(),
        task.updatedDate.toString(),
    )

}
