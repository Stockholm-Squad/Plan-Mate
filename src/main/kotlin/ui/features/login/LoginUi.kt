package org.example.ui.features.login

import org.example.logic.entities.User

import org.example.ui.features.common.ui_launcher.UiLauncher

interface LoginUi:UiLauncher{
   fun authenticateUser(): User?
   fun logout()
}