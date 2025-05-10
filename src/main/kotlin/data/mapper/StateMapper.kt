package org.example.data.mapper

import data.dto.ProjectStateDto
import org.example.logic.entities.ProjectState
import org.example.logic.utils.toSafeUUID

fun ProjectStateDto.mapToStateEntity(): ProjectState? {
    return ProjectState(id.toSafeUUID() ?: return null, name)
}


fun ProjectState.mapToStateModel(): ProjectStateDto {
    return ProjectStateDto(id.toString(), name)
}


