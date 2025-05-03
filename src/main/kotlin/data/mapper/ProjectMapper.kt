package org.example.data.mapper

import logic.model.entities.Project
import org.example.data.models.ProjectModel
import org.example.logic.usecase.extention.toSafeUUID

class ProjectMapper {
    fun mapToProjectEntity(projectModel: ProjectModel): Project =
        Project(projectModel.id.toSafeUUID(), projectModel.name, projectModel.stateId.toSafeUUID())

    fun mapToProjectModel(project: Project): ProjectModel =
        ProjectModel(project.id.toString(), project.name, project.stateId.toString())

}
