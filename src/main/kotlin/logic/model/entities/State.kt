package logic.model.entities

import java.util.*

data class State(
    val id: UUID = UUID.randomUUID(),
    val name: String
)
