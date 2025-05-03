package utils

import kotlinx.datetime.LocalDateTime
import logic.model.entities.Task
import org.example.data.models.TaskModel
import org.example.data.utils.DateHandlerImp
import java.util.UUID

fun buildTask(
    id: UUID= UUID.randomUUID(),
    name: String = "",
    description: String = "",
    stateId: UUID,
    createdDate: LocalDateTime = DateHandlerImp().getCurrentDateTime(),
    updatedDate: LocalDateTime = DateHandlerImp().getCurrentDateTime()

): Task {
    return Task(
        id, name, description,stateId, createdDate, updatedDate
    )
}


fun buildTaskModel(
    id: String = UUID.randomUUID().toString(),
    name: String = "",
    description: String = "",
    stateId: String = "",
    createdDate: String = DateHandlerImp().getCurrentDateTime().toString(),
    updatedDate: String = DateHandlerImp().getCurrentDateTime().toString()
): TaskModel {
    return TaskModel(id, name, description, stateId, createdDate, updatedDate)
}
