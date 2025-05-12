package org.example.ui.features.state.admin

import org.example.ui.features.common.ui_launcher.UiLauncher

interface AdminEntityStateManagerUi : UiLauncher {
    fun addState()
    fun updateState()
    fun deleteState()
    fun showAllStates()
}