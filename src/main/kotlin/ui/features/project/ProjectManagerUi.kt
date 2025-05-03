package org.example.ui.features.project

import org.example.ui.features.common.ui_launcher.UiLauncher

interface ProjectManagerUi: UiLauncher{
    fun showAllProjects()
    fun showProjectById()
    fun addProject()
    fun editProject()
    fun deleteProject()
    fun assignUsersToProject()
}