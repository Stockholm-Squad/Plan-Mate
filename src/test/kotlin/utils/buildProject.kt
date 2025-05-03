package utils

import logic.model.entities.Project
import java.util.UUID
fun buildProject(
    id: String = UUID.randomUUID().toString(),
    name: String = "",
    stateId: String = UUID.randomUUID().toString()
): Project {
    return Project(
        id = UUID.fromString(id),
        name = name,
        stateId = UUID.fromString(stateId)
    )
}