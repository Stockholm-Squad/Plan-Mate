package org.example.logic.entities

import java.util.*

data class EntityState(
    val id: UUID = UUID.randomUUID(),
    val name: String
)