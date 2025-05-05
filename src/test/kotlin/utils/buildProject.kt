package utils

import logic.models.entities.Project
import java.util.*

fun buildProject(
    id: UUID = UUID.randomUUID(),
    name: String = "",
    stateId: UUID = UUID.randomUUID()
): Project {
    return Project(
        id = id, name = name, stateId = stateId
    )
}