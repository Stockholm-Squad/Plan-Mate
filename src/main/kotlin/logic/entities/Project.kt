package org.example.logic.entities
import java.util.*

data class Project(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val stateId: UUID
)