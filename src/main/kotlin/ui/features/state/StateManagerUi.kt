package org.example.ui.features.state

import logic.model.entities.UserRole
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi

class StateManagerUi(
    private val adminStateManagerUi: AdminStateManagerUi,
    private val mateStateManagerUi: MateStateManagerUi,
    private val printer: OutputPrinter
) {
    fun launchStateManagerUi(userRole: UserRole?) {
        when (userRole) {
            UserRole.ADMIN -> adminStateManagerUi.launchUi()
            UserRole.MATE -> mateStateManagerUi.launchUi()
            else -> printer.showMessage("Invalid user")
        }
    }
}