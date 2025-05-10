package org.example.ui.features.state.mate

import org.example.ui.features.state.common.UserEntityStateManagerUi

class MateEntityStateManagerUiImpl(
    private val userEntityStateManagerUi: UserEntityStateManagerUi,
) : MateEntityStateManagerUi, UserEntityStateManagerUi {

    override fun launchUi() {
        this.showAllStates()
    }

    override fun showAllStates() {
        userEntityStateManagerUi.showAllStates()
    }
}