package org.example.ui.features.state.admin

import logic.model.entities.User
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.model.StateMenuChoice

class AdminStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
    private val manageStatesUseCase: ManageStatesUseCase,
    private val reader: InputReader,
    private val printer: OutputPrinter,
) : AdminStateManagerUi, UserStateManagerUi {

    override fun launchUi(user: User?) {
        while (true) {
            showMenu()
            if (handleMenuChoice()) break
        }
    }

    private fun showMenu() {
        printer.showMessage("What do you need ^_^")
        StateMenuChoice.entries.forEach { item ->
            printer.showMessage("${item.choiceNumber} ${item.choiceMessage}")
        }
    }

    private fun handleMenuChoice(): Boolean {
        when (reader.readIntOrNull()) {
            StateMenuChoice.SHOW_ALL.choiceNumber -> this.showAllStates()
            StateMenuChoice.ADD_STATE.choiceNumber -> this.addState()
            StateMenuChoice.EDIT_STATE.choiceNumber -> this.editState()
            StateMenuChoice.DELETE_STATE.choiceNumber -> this.deleteState()
            StateMenuChoice.BACK.choiceNumber -> return true
            else -> printer.showMessage("Please enter a valid choice!!")
        }
        return false
    }

    override fun addState() {
        printer.showMessage("Please enter name for the state:")
        reader.readStringOrNull()
            .takeIf { stateName -> stateName != null }
            ?.let { stateName ->
                manageStatesUseCase.addProjectState(stateName = stateName).fold(
                    onSuccess = { showAddStateMessage() },
                    onFailure = { showFailure("Failed to Add state: ${it.message}") }
                )
            } ?: showInvalidInput()
    }

    override fun editState() {
        printer.showMessage("Please enter the state you want to update: ")
        val currentStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                showInvalidInput()
                return
            }

        printer.showMessage("Please enter the new state name: ")
        val newStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                showInvalidInput()
                return
            }

        manageStatesUseCase.editProjectStateByName(
            stateName = currentStateName,
            newStateName = newStateName
        ).fold(
            onSuccess = { showStateUpdatedMessage() },
            onFailure = { showFailure("Failed to update state: ${it.message}") }
        )
    }

    private fun showStateUpdatedMessage() {
        printer.showMessage("State updated successfully ^_^")
    }

    private fun showAddStateMessage() {
        printer.showMessage("State added successfully ^_^")
    }

    private fun showFailure(errorMessage: String) {
        printer.showMessage(errorMessage)
    }

    private fun showInvalidInput() {
        printer.showMessage("Invalid input")
    }

    override fun deleteState() {
        printer.showMessage("Please enter the state you want to delete: ")
        reader.readStringOrNull().takeIf { stateName ->
            stateName != null
        }?.let { stateName ->
            manageStatesUseCase.deleteProjectState(stateName = stateName).fold(
                onSuccess = { showStateDeletedMessage() },
                onFailure = { showFailure("Failed to Delete state: ${it.message}") }
            )
        } ?: showInvalidInput()
    }

    private fun showStateDeletedMessage() {
        printer.showMessage("State deleted successfully ^_^")
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}