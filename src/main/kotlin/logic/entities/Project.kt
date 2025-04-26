package org.example.logic.entities


import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val state: State,
    val task: MutableList<Task>
)