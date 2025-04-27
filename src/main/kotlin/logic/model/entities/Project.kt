package logic.model.entities


import java.util.*

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val stateId: String
)