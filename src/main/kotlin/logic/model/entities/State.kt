package logic.model.entities

import java.util.*

data class State(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
)
