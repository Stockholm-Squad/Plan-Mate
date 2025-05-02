package org.example.data.mapper

import logic.model.entities.Project


fun org.example.data.models.Project.mapToProjectEntity(): Project = Project(id.toSafeUUID(),name,stateId)