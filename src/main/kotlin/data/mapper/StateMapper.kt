package org.example.data.mapper

import logic.model.entities.Project
import org.example.data.models.State

class StateMapper {
    fun mapToStateEntity(state:State): logic.model.entities.State = logic.model.entities.State(state.id.toSafeUUID(),state.name)
    fun mapToStateModel(state: logic.model.entities.State): State = State(state.id.toString(),state.name)

}
