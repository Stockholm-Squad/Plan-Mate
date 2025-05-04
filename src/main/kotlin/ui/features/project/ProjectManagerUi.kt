package org.example.ui.features.project

import logic.model.entities.User
import org.example.ui.features.common.ui_launcher.UiLauncher

interface ProjectManagerUi: UiLauncher{
    fun showAllProjects()
    fun showProjectByName()
    fun addProject()
    fun editProject()
    fun deleteProject()
    fun assignUsersToProject(user: User?)
    fun showUsersAssignedToProject()
    fun removeUserFromProject()
}