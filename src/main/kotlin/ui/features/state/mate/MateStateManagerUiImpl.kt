package org.example.ui.features.state.mate

import logic.models.entities.User
import org.example.ui.features.state.common.UserStateManagerUi

class MateStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
) : MateStateManagerUi, UserStateManagerUi {

    override suspend fun launchUi(user: User?) {
        this.showAllStates()
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}