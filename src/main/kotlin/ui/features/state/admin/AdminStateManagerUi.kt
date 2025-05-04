package org.example.ui.features.state.admin

import logic.model.entities.User
import org.example.ui.features.common.ui_launcher.UiLauncher

interface AdminStateManagerUi : UiLauncher {
    fun addState(user: User)
    fun editState(user: User)
    fun deleteState(user: User)
    fun showAllStates()
}