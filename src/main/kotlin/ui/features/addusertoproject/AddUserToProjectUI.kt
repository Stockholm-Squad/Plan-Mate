package org.example.ui.features.addusertoProject

import org.example.logic.entities.User

interface AddUserToProjectUI {
    fun invoke(user: User?)
    fun assignUsersToProject(user: User?)
    fun showUsersAssignedToProject()
    fun removeUserFromProject()
}