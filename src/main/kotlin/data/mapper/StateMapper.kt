package org.example.data.mapper

import logic.models.entities.ProjectState
import org.example.data.models.ProjectStateModel
import org.example.logic.usecase.extention.toSafeUUID

fun ProjectStateModel.mapToStateEntity(): ProjectState? {
    return try {
        ProjectState(id.toSafeUUID(), name)
    } catch (throwable: Throwable) {
        null
    }
}

fun ProjectState.mapToStateModel(): ProjectStateModel =
    ProjectStateModel(id.toString(), name)


