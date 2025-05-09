package org.example.ui.features.state

import logic.models.entities.User
import logic.models.entities.UserRole
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.input_output.output.OutputPrinter

class StateManagerUiImp(
    private val adminStateManagerUi: AdminStateManagerUi,
    private val mateStateManagerUi: MateStateManagerUi,
    private val printer: OutputPrinter
) : StateManageUi {
    override fun launchStateManagerUi(user: User?) {
        when (user?.userRole) {
            UserRole.ADMIN -> adminStateManagerUi.launchUi(user)
            UserRole.MATE -> mateStateManagerUi.launchUi(user)
            else -> printer.showMessage(UiMessages.INVALID_USER)
        }
    }

    override fun launchUi(user: User?) {
        launchStateManagerUi(user = user)
    }
}