package utils

import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.Task
import data.dto.TaskDto
import org.example.data.utils.DateHandlerImp
import java.util.*

fun buildTask(
    id: UUID = UUID.randomUUID(),
    projectName: String = "",
    name: String = "",
    description: String = "",
    stateId: UUID = UUID.randomUUID(),
    createdDate: LocalDateTime = DateHandlerImp().getCurrentDateTime(),
    updatedDate: LocalDateTime = DateHandlerImp().getCurrentDateTime()

): Task {
    return Task(
        id, projectName, name, description, stateId, createdDate, updatedDate
    )
}


fun buildTaskModel(
    id: String = UUID.randomUUID().toString(),
    projectName: String = "",
    name: String = "",
    description: String = "",
    stateId: String = "",
    createdDate: String = DateHandlerImp().getCurrentDateTime().toString(),
    updatedDate: String = DateHandlerImp().getCurrentDateTime().toString()
): TaskDto {
    return TaskDto(id, projectName, name, description, stateId, createdDate, updatedDate)
}
