package utils

import logic.model.entities.Project
import java.util.UUID

fun buildProject(
    name: String = "",
): Project {
    return Project(
       name= name, stateId = UUID.randomUUID()
    )
}