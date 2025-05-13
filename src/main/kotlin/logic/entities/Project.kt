package org.example.logic.entities
import java.util.*

data class Project(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val stateId: UUID
)