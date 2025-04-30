package org.example.ui.features.state

import logic.model.entities.Role
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi

class StateManagerUi(
    private val adminStateManagerUi: AdminStateManagerUi,
    private val mateStateManagerUi: MateStateManagerUi
) {
    fun launchStateManagerUi(userRole: Role) {
        when (userRole) {
            Role.ADMIN -> adminStateManagerUi.launchUi()
            Role.MATE -> mateStateManagerUi.launchUi()
        }
    }
}