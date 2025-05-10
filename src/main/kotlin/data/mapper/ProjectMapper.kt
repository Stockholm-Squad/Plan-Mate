package org.example.data.mapper

import data.dto.ProjectDto
import org.example.logic.entities.Project
import org.example.logic.utils.toSafeUUID

fun ProjectDto.mapToProjectEntity(): Project? {
    return Project(
        id.toSafeUUID() ?: return null,
        name,
        stateId.toSafeUUID() ?: return null
    )
}

fun Project.mapToProjectModel(): ProjectDto {
    return ProjectDto(id.toString(), name, stateId.toString())
}


