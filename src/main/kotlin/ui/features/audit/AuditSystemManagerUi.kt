package org.example.ui.features.audit

import org.example.data.repo.AuditSystemRepositoryImp
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.utils.Constant
import org.example.utils.SearchUtils

class AuditSystemManagerUi(
    private val getAuditSystemUseCase: ManageAuditSystemUseCase,
    private val auditSystemRepositoryImp: AuditSystemRepositoryImp,
    private val searchUtils: SearchUtils,
    private val printer: OutputPrinter,
    private val reader: InputReader,

    ) {

    fun showAuditSystemManagerUI() {
        var shouldContinue: Boolean

        do {
            displayMainMenu()
            val option = searchUtils.getMainMenuOption()

            when (option) {
                1 -> displayAllAuditSystems()
                2 -> searchAuditSystemById()
                3 -> searchAuditSystemsByType()
                4 -> searchAuditSystemsByEntityId()
                5 -> addNewAuditSystem()
                6 -> printer.showMessage(Constant.EXITING)
                else -> printer.showMessage(Constant.INVALID_SELECTION_MESSAGE)
            }

            shouldContinue = searchUtils.shouldSearchAgain(reader) == true
        } while (shouldContinue)
    }


    private fun displayMainMenu() {
        // TODO
    }


    private fun displayAllAuditSystems() {
        // TODO
    }


    private fun searchAuditSystemById() {
        // TODO
    }



    private fun searchAuditSystemsByType() {
        // TODO
    }


    private fun searchAuditSystemsByEntityId() {
        // TODO
    }


    private fun addNewAuditSystem() {
        // TODO
    }
}