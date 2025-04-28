package org.example.ui.features.state

import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUiImp

class AdminStateManagerUiImpl(
    private val manageStatesUseCase: ManageStatesUseCase,
) : AdminStateManagerUi, UserStateManagerUiImp(manageStatesUseCase) {

    override fun launchUi() {
        TODO("Not yet implemented")
    }

    override fun addState() {
        TODO("Not yet implemented")
    }

    override fun editState() {
        TODO("Not yet implemented")
    }

    override fun deleteState() {
        TODO("Not yet implemented")
    }

    override fun showAllStates() {
        TODO("Not yet implemented")
    }
}