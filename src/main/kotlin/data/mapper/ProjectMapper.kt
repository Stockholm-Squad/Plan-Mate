package org.example.data.mapper

import logic.model.entities.Project
import org.example.data.extention.toSafeUUID

class ProjectMapper {
    fun mapToProjectEntity(project: org.example.data.models.Project): Project =
        Project(project.id.toSafeUUID(), project.name, project.stateId.toSafeUUID())

    fun mapToProjectModel(project: Project): org.example.data.models.Project =
        org.example.data.models.Project(project.id.toString(), project.name, project.stateId.toString())

}
