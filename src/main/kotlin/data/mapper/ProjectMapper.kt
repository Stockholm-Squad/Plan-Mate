package org.example.data.mapper

import data.dto.ProjectDto
import org.example.logic.entities.Project
import org.example.logic.utils.toSafeUUID

fun ProjectDto.mapToProjectEntity(): Project? =

    Project(id.toSafeUUID(), name, stateId.toSafeUUID())

fun Project.mapToProjectModel(): ProjectDto =
    ProjectDto(id.toString(), name, stateId.toString())


