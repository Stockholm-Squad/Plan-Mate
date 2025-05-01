package org.example.ui.features.state

import org.example.ui.features.common.ui_launcher.UiLauncher

interface AdminStateManagerUi : UiLauncher {
    fun addState()
    fun editState()
    fun deleteState()
    fun showAllStates()
}