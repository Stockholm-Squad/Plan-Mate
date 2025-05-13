package data.repo

import data.dto.EntityStateDto
import org.example.logic.entities.EntityState
import java.util.UUID

val entityStateId = UUID.randomUUID()
val entityState = EntityState(
    id = entityStateId,
    title = "Todo"
)
val entityStateDto = EntityStateDto(
    id = entityStateId.toString(),
    title = "Todo"
)
