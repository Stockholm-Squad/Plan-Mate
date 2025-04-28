package org.example.ui.features.state

import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUiImp

class MateStateManagerUiImpl(
    private val manageStatesUseCase: ManageStatesUseCase
) : MateStateManagerUi, UserStateManagerUiImp(manageStatesUseCase) {

    override fun launchUi() {
        TODO("Not yet implemented")
    }

    override fun showAllStates() {
        TODO("Not yet implemented")
    }
}