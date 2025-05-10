package org.example.data.mapper

import org.example.logic.entities.Project
import data.dto.ProjectModel
import org.example.logic.usecase.extention.toSafeUUID

fun ProjectModel.mapToProjectEntity(): Project? =
    try {
        Project(id.toSafeUUID(), name, stateId.toSafeUUID())
    } catch (throwable: Throwable) {
        null
    }

fun Project.mapToProjectModel(): ProjectModel =
    ProjectModel(id.toString(), name, stateId.toString())


