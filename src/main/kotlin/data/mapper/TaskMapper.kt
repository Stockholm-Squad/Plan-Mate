package org.example.data.mapper

import org.example.data.models.State
import org.example.data.models.Task

class TaskMapper {
    fun mapToTaskEntity(task: Task): logic.model.entities.Task = logic.model.entities.Task(
        task.id.toSafeUUID(),
        task.name,
        task.description,
        task.stateId.toSafeUUID(),
        task.createdDate.toLocalDateTime(),
        task.updatedDate.toLocalDateTime()
    )

    fun mapToTaskModel(task: logic.model.entities.Task): Task = Task(task.id.toString(),task.name.toString(),task.description.toString(),task.stateId.toString(),task.createdDate.toString(),task.updatedDate.toString(),)

}
