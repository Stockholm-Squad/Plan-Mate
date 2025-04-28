package utils

import logic.model.entities.Project
import java.util.UUID

fun buildProject(
    id: String = UUID.randomUUID().toString(),
    name: String = "",
    stateId: String = ""
): Project {
    return Project(
        id, name, stateId
    )
}