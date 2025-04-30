package org.example.ui.features.state.admin

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.model.StateMenuChoice

class AdminStateManagerUiImpl(
    private val userStateManagerUi: UserStateManagerUi,
    private val manageStatesUseCase: ManageStatesUseCase,
    private val reader: InputReader,
    private val printer: OutputPrinter
) : AdminStateManagerUi, UserStateManagerUi {

    override fun launchUi() {
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
        println()
    }

    override fun editState() {
        println()
    }

    override fun deleteState() {
        println()
    }

    override fun showAllStates() {
        userStateManagerUi.showAllStates()
    }
}