package org.example.data.mapper

import logic.model.entities.Project
import org.example.data.models.ProjectModel
import org.example.logic.usecase.extention.toSafeUUID

fun ProjectModel.mapToProjectEntity(): Project =
    Project(id.toSafeUUID(), name, stateId.toSafeUUID())

fun Project.mapToProjectModel(): ProjectModel =
    ProjectModel(id.toString(), name, stateId.toString())


