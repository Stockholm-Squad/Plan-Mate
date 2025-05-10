package org.example.ui.features.state.admin

import kotlinx.coroutines.runBlocking
import org.example.logic.entities.User
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.common.UserEntityStateManagerUi
import org.example.ui.features.state.model.EntityStateMenuChoice
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class AdminEntityEntityStateManagerUiImpl(
    private val userStateManagerUi: UserEntityStateManagerUi,
    private val manageStatesUseCase: ManageEntityStatesUseCase,
    private val reader: InputReader,
    private val printer: OutputPrinter,
) : AdminEntityStateManagerUi, UserEntityStateManagerUi {

    override fun launchUi(user: User?) {
        while (true) {
            showMenu()
            if (handleMenuChoice()) break
        }
    }

    private fun showMenu() {
        printer.showMessage("-------------------------------------")
        printer.showMessage(UiMessages.WHAT_DO_YOU_NEED)
        EntityStateMenuChoice.entries.forEach { item ->
            printer.showMessage("${item.choiceNumber} ${item.choiceMessage}")
        }
        printer.showMessage("-------------------------------------")
    }

    private fun handleMenuChoice(): Boolean {
        when (reader.readIntOrNull()) {
            EntityStateMenuChoice.SHOW_ALL.choiceNumber -> this.showAllStates()
            EntityStateMenuChoice.ADD_STATE.choiceNumber -> this.addState()
            EntityStateMenuChoice.EDIT_STATE.choiceNumber -> this.editState()
            EntityStateMenuChoice.DELETE_STATE.choiceNumber -> this.deleteState()
            EntityStateMenuChoice.BACK.choiceNumber -> return true
            else -> printer.showMessage("Please enter a valid choice!!")
        }
        return false
    }

    override fun addState() {
        printer.showMessage(UiMessages.PLEASE_ENTER_NAME_FOR_THE_STATE)
        reader.readStringOrNull()
            .takeIf { stateName -> stateName != null }
            ?.let { stateName ->
                runBlocking {
                    try {
                        manageStatesUseCase.addEntityState(stateName = stateName)
                        printer.showMessage(UiMessages.STATE_ADDED_SUCCESSFULLY)
                    } catch (exception: Exception) {
                        printer.showMessage("Failed to delete state: ${exception.message}")
                    }
                }
            } ?: printer.showMessage(UiMessages.INVALID_INPUT)
    }

    override fun editState() {
        printer.showMessage(UiMessages.PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_UPDATE)
        val currentStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                printer.showMessage(UiMessages.INVALID_INPUT)
                return
            }

        printer.showMessage(UiMessages.PLEASE_ENTER_THE_NEW_STATE_NAME)
        val newStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                printer.showMessage(UiMessages.INVALID_INPUT)
                return
            }
        runBlocking {
            try {
                manageStatesUseCase.editEntityStateByName(
                    stateName = currentStateName,
                    newStateName = newStateName
                )
                printer.showMessage(UiMessages.STATE_UPDATED_SUCCESSFULLY)
            } catch (exception: Exception) {
                printer.showMessage("Failed to update state: ${exception.message}")
            }
        }
    }

    override fun deleteState() {
        printer.showMessage(UiMessages.PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_DELETE)
        reader.readStringOrNull().takeIf { stateName ->
            stateName != null
        }?.let { stateName ->
            runBlocking {
                try {
                    manageStatesUseCase.deleteEntityState(stateName = stateName)
                    showStateDeletedMessage()
                } catch (exception: Exception) {
                    printer.showMessage("Failed to delete state: ${exception.message}")
                }
            }
        } ?: printer.showMessage(UiMessages.INVALID_INPUT)
    }

    private fun showStateDeletedMessage() {
        printer.showMessage(UiMessages.STATE_DELETED_SUCCESSFULLY)
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}