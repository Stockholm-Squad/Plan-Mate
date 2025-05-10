package org.example.data.mapper

import data.dto.ProjectStateDto
import org.example.logic.entities.ProjectState
import org.example.logic.usecase.extention.toSafeUUID

fun ProjectStateDto.mapToStateEntity(): ProjectState? =

    ProjectState(id.toSafeUUID(), name)


fun ProjectState.mapToStateModel(): ProjectStateDto =
    ProjectStateDto(id.toString(), name)


