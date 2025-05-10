package org.example.ui.features.state.mate

import org.example.logic.entities.User
import org.example.ui.features.state.common.UserEntityStateManagerUi

class MateEntityEntityStateManagerUiImpl(
    private val userEntityStateManagerUi: UserEntityStateManagerUi,
) : MateEntityStateManagerUi, UserEntityStateManagerUi {

    override fun launchUi(user: User?) {
        this.showAllStates()
    }

    override fun showAllStates() {
        userEntityStateManagerUi.showAllStates()
    }
}