package org.example.ui.features.login

import logic.model.entities.User

import org.example.ui.features.common.ui_launcher.UiLauncher

interface LoginUi:UiLauncher{
    fun authenticateUser(): User?
}