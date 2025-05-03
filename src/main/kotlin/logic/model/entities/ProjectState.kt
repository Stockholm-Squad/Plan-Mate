package logic.model.entities

import java.util.*

data class ProjectState(
    val id: UUID = UUID.randomUUID(),
    val name: String
)