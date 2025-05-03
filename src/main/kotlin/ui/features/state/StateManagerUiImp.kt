package org.example.ui.features.state

import logic.model.entities.User
import logic.model.entities.UserRole
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi

class StateManagerUiImp(
    private val adminStateManagerUi: AdminStateManagerUi,
    private val mateStateManagerUi: MateStateManagerUi,
    private val printer: OutputPrinter
) :StateManageUi{
  override  fun launchStateManagerUi(user: User?) {
        when (user?.userRole) {
            UserRole.ADMIN -> adminStateManagerUi.launchUi(user)
            UserRole.MATE -> mateStateManagerUi.launchUi(user)
            else -> printer.showMessage("Invalid user")
        }
    }

    override fun launchUi(user: User?) {
        launchStateManagerUi(user = user)
    }
}