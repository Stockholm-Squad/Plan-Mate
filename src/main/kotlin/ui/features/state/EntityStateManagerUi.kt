package org.example.ui.features.state

import logic.usecase.login.LoginUseCase
import org.example.logic.entities.UserRole
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.output.OutputPrinter

class EntityStateManagerUi(
    private val adminEntityStateManagerUi: AdminEntityStateManagerUi,
    private val showAllEntityStateManagerUi: ShowAllEntityStateManagerUi,
    private val printer: OutputPrinter,
    private val loginUseCase: LoginUseCase,
) : UiLauncher {

    override fun launchUi() {
        val user = loginUseCase.getCurrentUser()
        when (user?.userRole) {
            UserRole.ADMIN -> adminEntityStateManagerUi.launchUi()
            UserRole.MATE -> showAllEntityStateManagerUi.launchUi()
            else -> printer.showMessageLine(UiMessages.INVALID_USER)
        }
    }
}