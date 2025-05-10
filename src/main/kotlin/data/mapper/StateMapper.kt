package org.example.data.mapper

import org.example.logic.entities.ProjectState
import data.dto.ProjectStateDto
import org.example.logic.usecase.extention.toSafeUUID

fun ProjectStateDto.mapToStateEntity(): ProjectState? {
    return try {
        ProjectState(id.toSafeUUID(), name)
    } catch (throwable: Throwable) {
        null
    }
}

fun ProjectState.mapToStateModel(): ProjectStateDto =
    ProjectStateDto(id.toString(), name)


