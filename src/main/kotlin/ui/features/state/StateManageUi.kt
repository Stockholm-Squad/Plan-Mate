package org.example.ui.features.state

import logic.model.entities.User
import org.example.ui.features.common.ui_launcher.UiLauncher

interface StateManageUi : UiLauncher {
    fun launchStateManagerUi(user: User?)
}