package org.example.ui.features.state.mate

import org.example.logic.entities.User
import org.example.ui.features.state.common.UserStateManagerUi

class MateStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
) : MateStateManagerUi, UserStateManagerUi {

    override fun launchUi(user: User?) {
        this.showAllStates()
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}