package org.example.ui.features.common.ui_launcher

import logic.models.entities.User

interface UiLauncher {
    suspend fun launchUi(user: User?)
}