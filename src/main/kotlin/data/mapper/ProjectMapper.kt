package org.example.data.mapper

import org.example.logic.entities.Project
import data.dto.ProjectDto
import org.example.logic.usecase.extention.toSafeUUID

fun ProjectDto.mapToProjectEntity(): Project? =
    try {
        Project(id.toSafeUUID(), name, stateId.toSafeUUID())
    } catch (throwable: Throwable) {
        null
    }

fun Project.mapToProjectModel(): ProjectDto =
    ProjectDto(id.toString(), name, stateId.toString())


