package org.example.ui.features.state

import logic.usecase.login.LoginUseCase
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.input_output.output.OutputPrinter

class StateManagerUiImp(
    private val adminStateManagerUi: AdminStateManagerUi,
    private val mateStateManagerUi: MateStateManagerUi,
    private val printer: OutputPrinter,
    private val loginUseCase: LoginUseCase,
) : StateManageUi {
    override fun launchStateManagerUi() {
        val user = loginUseCase.getCurrentUser()
        when (user?.userRole) {
            UserRole.ADMIN -> adminStateManagerUi.launchUi()
            UserRole.MATE -> mateStateManagerUi.launchUi()
            else -> printer.showMessage(UiMessages.INVALID_USER)
        }
    }

    override fun launchUi() {
        launchStateManagerUi()
    }
}