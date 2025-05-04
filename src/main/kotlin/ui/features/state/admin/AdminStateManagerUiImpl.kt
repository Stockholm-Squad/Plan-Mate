package org.example.ui.features.state.admin

import logic.model.entities.User
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.model.StateMenuChoice
import org.example.ui.utils.UiMessages

class AdminStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
    private val manageStatesUseCase: ManageStatesUseCase,
    private val reader: InputReader,
    private val printer: OutputPrinter,
) : AdminStateManagerUi, UserStateManagerUi {

    override fun launchUi(user: User?) {
        while (true) {
            showMenu()
            if (user != null) {
                if (handleMenuChoice(user)) break
            }else{
                printer.showMessage("Sorry user cannot be found.")
            }
        }
    }

    private fun showMenu() {
        printer.showMessage(UiMessages.WHAT_DO_YOU_NEED)
        StateMenuChoice.entries.forEach { item ->
            printer.showMessage("${item.choiceNumber} ${item.choiceMessage}")
        }
    }

    private fun handleMenuChoice(user:User): Boolean {
        when (reader.readIntOrNull()) {
            StateMenuChoice.SHOW_ALL.choiceNumber -> this.showAllStates()
            StateMenuChoice.ADD_STATE.choiceNumber -> this.addState(user)
            StateMenuChoice.EDIT_STATE.choiceNumber -> this.editState(user)
            StateMenuChoice.DELETE_STATE.choiceNumber -> this.deleteState(user)
            StateMenuChoice.BACK.choiceNumber -> return true
            else -> printer.showMessage("Please enter a valid choice!!")
        }
        return false
    }

    override fun addState(user: User) {
        printer.showMessage(UiMessages.PLEASE_ENTER_NAME_FOR_THE_STATE)
        reader.readStringOrNull()
            .takeIf { stateName -> stateName != null }
            ?.let { stateName ->
                manageStatesUseCase.addProjectState(stateName = stateName, userId = user.id).fold(
                    onSuccess = { showAddStateMessage() },
                    onFailure = { showFailure("${UiMessages.FAILED_TO_ADD_STATE}${it.message}") }
                )
            } ?: showInvalidInput()
    }

    override fun editState(user: User) {
        printer.showMessage(UiMessages.PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_UPDATE)
        val currentStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                showInvalidInput()
                return
            }

        printer.showMessage(UiMessages.PLEASE_ENTER_THE_NEW_STATE_NAME)
        val newStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                showInvalidInput()
                return
            }

        manageStatesUseCase.editProjectStateByName(
            stateName = currentStateName,
            newStateName = newStateName, userId = user.id
        ).fold(
            onSuccess = { showStateUpdatedMessage() },
            onFailure = { showFailure("${UiMessages.FAILED_TO_EDIT_STATE} ${it.message}") }
        )
    }

    private fun showStateUpdatedMessage() {
        printer.showMessage(UiMessages.STATE_UPDATED_SUCCESSFULLY)
    }

    private fun showAddStateMessage() {
        printer.showMessage(UiMessages.STATE_ADDED_SUCCESSFULLY)
    }

    private fun showFailure(errorMessage: String) {
        printer.showMessage(errorMessage)
    }

    private fun showInvalidInput() {
        printer.showMessage(UiMessages.INVALID_INPUT)
    }

    override fun deleteState(user: User) {
        printer.showMessage(UiMessages.PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_DELETE)
        reader.readStringOrNull().takeIf { stateName ->
            stateName != null
        }?.let { stateName ->
            manageStatesUseCase.deleteProjectState(stateName = stateName, userId = user.id).fold(
                onSuccess = { showStateDeletedMessage() },
                onFailure = { showFailure("${UiMessages.FAILED_TO_DELETE_STATE}${it.message}") }
            )
        } ?: showInvalidInput()
    }

    private fun showStateDeletedMessage() {
        printer.showMessage(UiMessages.STATE_DELETED_SUCCESSFULLY)
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}