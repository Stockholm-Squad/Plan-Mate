package org.example.ui.features.state.common

import kotlinx.coroutines.runBlocking
import org.example.logic.entities.EntityState
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.input_output.output.OutputPrinter

class UserEntityStateManagerUiImp(
    private val manageStatesUseCase: ManageEntityStatesUseCase,
    private val printer: OutputPrinter
) : UserEntityStateManagerUi {

    override fun showAllStates() {
        runBlocking {
            try {
                manageStatesUseCase.getAllEntityStates().also {
                    handleSuccess(it)
                }
            } catch (exception: Exception) {
                handleFailure(exception)
            }
        }
    }

    private fun handleFailure(throwable: Throwable) {
        printer.showMessage("Failed to Load data, ${throwable.message}")
    }

    private fun handleSuccess(projectStates: List<EntityState>) {
        printer.showStates(projectStates)
    }
}