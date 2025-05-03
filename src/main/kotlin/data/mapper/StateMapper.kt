package org.example.data.mapper

import org.example.data.models.State
import org.example.data.extention.toSafeUUID

class StateMapper {
    fun mapToStateEntity(state: State): logic.model.entities.ProjectState =
        logic.model.entities.ProjectState(state.id.toSafeUUID(), state.name)

    fun mapToStateModel(projectState: logic.model.entities.ProjectState): State = State(projectState.id.toString(), projectState.name)

}
