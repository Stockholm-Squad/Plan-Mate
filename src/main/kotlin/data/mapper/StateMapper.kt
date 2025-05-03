package org.example.data.mapper

import logic.model.entities.ProjectState
import org.example.data.models.State
import org.example.logic.usecase.extention.toSafeUUID

fun State.mapToStateEntity(): ProjectState =
    ProjectState(id.toSafeUUID(), name)

fun ProjectState.mapToStateModel(): State =
    State(id.toString(), name)


