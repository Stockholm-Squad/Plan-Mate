package utils

import org.example.logic.entities.Project
import java.util.*

fun buildProject(
    id: UUID = UUID.randomUUID(),
    name: String = "",
    stateId: UUID = UUID.randomUUID()
): Project {
    return Project(
        id = id, title = name, stateId = stateId
    )
}