package utils

import data.dto.TaskDto
import kotlinx.datetime.LocalDateTime
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Task
import java.util.*

fun buildTask(
    id: UUID = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa1"),
    projectName: String = "Project",
    title: String = "",
    description: String = "",
    stateId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1"),
    createdDate: LocalDateTime = DateHandlerImp().getCurrentDateTime(),
    updatedDate: LocalDateTime = DateHandlerImp().getCurrentDateTime()

): Task {
    return Task(
        id, projectName, title, description, stateId, createdDate, updatedDate
    )
}


fun buildTaskModel(
    id: String = UUID.randomUUID().toString(),
    projectName: String = "",
    title: String = "",
    description: String = "",
    stateId: String = "",
    createdDate: String = DateHandlerImp().getCurrentDateTime().toString(),
    updatedDate: String = DateHandlerImp().getCurrentDateTime().toString()
): TaskDto {
    return TaskDto(id, projectName, title, description, stateId, createdDate, updatedDate)
}
