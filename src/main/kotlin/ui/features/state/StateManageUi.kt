package org.example.ui.features.state

import logic.models.entities.User
import org.example.ui.features.common.ui_launcher.UiLauncher

interface StateManageUi : UiLauncher {
    suspend fun launchStateManagerUi(user: User?)
}