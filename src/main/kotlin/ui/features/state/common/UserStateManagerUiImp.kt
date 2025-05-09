package org.example.ui.features.state.common

import kotlinx.coroutines.runBlocking
import logic.models.entities.ProjectState
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.input_output.output.OutputPrinter

class UserStateManagerUiImp(
    private val manageStatesUseCase: ManageStatesUseCase,
    private val printer: OutputPrinter
) : UserStateManagerUi {

    override fun showAllStates() {
        runBlocking {
            try {
                manageStatesUseCase.getAllProjectStates().also {
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

    private fun handleSuccess(projectStates: List<ProjectState>) {
        printer.showStates(projectStates)
    }
}