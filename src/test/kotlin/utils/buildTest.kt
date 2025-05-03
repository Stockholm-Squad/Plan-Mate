package utils

import kotlinx.datetime.LocalDateTime
import logic.model.entities.Task
import org.example.ui.utils.DateHandlerImp
import java.util.UUID

fun buildTask(
    id: String = UUID.randomUUID().toString(),
    name: String = "",
    description: String = "",
    stateId: String = "",
    createdDate: LocalDateTime = DateHandlerImp().getCurrentDateTime(),
    updatedDate: LocalDateTime = DateHandlerImp().getCurrentDateTime()

): Task {
    return Task(
        id, name, description,stateId, createdDate, updatedDate
    )
}
