package org.example.ui.features.state.mate

import org.example.ui.features.state.common.UserStateManagerUi

class MateStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
) : MateStateManagerUi, UserStateManagerUi {

    override fun launchUi() {
        this.showAllStates()
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}