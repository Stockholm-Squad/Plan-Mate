package org.example.data.mapper

import org.example.data.models.State
import org.example.data.models.Task

class TaskMapper {
    fun mapToTaskEntity(task: Task): logic.model.entities.Task = logic.model.entities.Task(
        task.id.toSafeUUID(),
        task.name,
        task.description,
        task.stateId,
        task.createdDate.toLocalDateTime(),
        task.updatedDate.toLocalDateTime()
    )

    fun mapToTaskModel(state: logic.model.entities.State): State = State(state.id.toString(), state.name)

}
