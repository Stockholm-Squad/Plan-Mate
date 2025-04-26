package org.example.logic.entities

data class Project(
    val id: String,
    val name: String,
    val state: State,
    val task: MutableList<Task>
)