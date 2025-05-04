package utils

import logic.model.entities.Project
import java.util.UUID

fun buildProject(
    id: UUID = UUID.randomUUID(),
    name: String = "",
    stateId: UUID = UUID.randomUUID()
): Project {
    return Project(
        id = id, name = name, stateId = stateId
    )
}