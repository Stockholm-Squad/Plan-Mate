package org.example.data.mapper

import logic.model.entities.ProjectState
import org.example.data.models.State
import org.example.logic.usecase.extention.toSafeUUID

fun State.mapToStateEntity(): ProjectState? {
    return try {
        ProjectState(id.toSafeUUID(), name)
    } catch (throwable: Throwable) {
        null
    }
}

fun ProjectState.mapToStateModel(): State =
    State(id.toString(), name)


