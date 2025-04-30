package org.example.ui.features.state.common

import logic.model.entities.State
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase

open class UserStateManagerUiImp(
    private val manageStatesUseCase: ManageStatesUseCase,
    private val printer: OutputPrinter
) : UserStateManagerUi {
    override fun showAllStates() {
        manageStatesUseCase.getAllStates().fold(
            onSuccess = ::handleSuccess,
            onFailure = ::handleFailure
        )
    }

    private fun handleFailure(throwable: Throwable) {
        printer.showMessage("Failed to Load data, ${throwable.message}")
        printer.showMessage("Please try again ^_^")
    }

    private fun handleSuccess(states: List<State>) {
        printer.showStates(states)
    }
}