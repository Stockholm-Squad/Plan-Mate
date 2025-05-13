package org.example.data.mapper

import data.dto.ProjectDto
import org.example.logic.entities.Project
import org.example.logic.utils.toSafeUUID

fun ProjectDto.mapToProjectEntity(): Project? {
    return Project(
        id = id.toSafeUUID() ?: return null,
        title = title,
        stateId = stateId.toSafeUUID() ?: return null
    )
}

fun Project.mapToProjectModel(): ProjectDto {
    return ProjectDto(
        id = id.toString(),
        title = title,
        stateId = stateId.toString()
    )
}


