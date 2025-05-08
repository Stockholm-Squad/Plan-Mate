package org.example.ui.features.state.common

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.models.entities.ProjectState
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.input_output.output.OutputPrinter

class UserStateManagerUiImp(
    private val manageStatesUseCase: ManageStatesUseCase,
    private val printer: OutputPrinter
) : UserStateManagerUi {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleFailure(throwable)
    }

    override fun showAllStates() {
        runBlocking(errorHandler) {
            manageStatesUseCase.getAllProjectStates().also {
                handleSuccess(it)
            }
        }
    }

    private fun handleFailure(throwable: Throwable) {
        printer.showMessage("Failed to Load data, ${throwable.message}")
        printer.showMessage("Please try again ^_^")
    }

    private fun handleSuccess(projectStates: List<ProjectState>) {
        printer.showStates(projectStates)
    }
}