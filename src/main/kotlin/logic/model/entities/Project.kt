package logic.model.entities


import java.util.*

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val state: State,
    val task: MutableList<Task>
)