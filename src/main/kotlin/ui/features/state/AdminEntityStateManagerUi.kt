package org.example.ui.features.state

import kotlinx.coroutines.runBlocking
import org.example.logic.entities.EntityType
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter


class AdminEntityStateManagerUi(
    private val showAllEntityStateManagerUi: ShowAllEntityStateManagerUi,
    private val manageEntityStatesUseCase: ManageEntityStatesUseCase,
    private val auditServicesUseCase: AuditServicesUseCase,
    private val reader: InputReader,
    private val printer: OutputPrinter,
) : UiLauncher {

    override fun launchUi() {
        while (true) {
            printer.showMessageLine(UiMessages.SHOW_ADMIN_ENTITY_STATE_OPTIONS)
            if (handleMenuChoice()) break
        }
    }

    private fun handleMenuChoice(): Boolean {
        when (reader.readIntOrNull()) {
            1 -> showAllEntityStateManagerUi.launchUi()
            2 -> this.addState()
            3 -> this.updateState()
            4 -> this.deleteState()
            0 -> return true
            else -> printer.showMessageLine(UiMessages.INVALID_SELECTION_MESSAGE)
        }
        return false
    }

    fun addState() {
        printer.showMessageLine(UiMessages.PLEASE_ENTER_NAME_FOR_THE_STATE)
        reader.readStringOrNull()
            .takeIf { stateName -> stateName != null }
            ?.let { stateName ->
                runBlocking {
                    try {
                        manageEntityStatesUseCase.addEntityState(stateName = stateName)
                        val stateId = manageEntityStatesUseCase.getEntityStateIdByName(stateName)
                        auditServicesUseCase.addAuditForAddEntity(
                            entityType = EntityType.STATE,
                            entityName = stateName,
                            entityId = stateId,
                        )
                        printer.showMessageLine(UiMessages.STATE_ADDED_SUCCESSFULLY)
                    } catch (exception: Exception) {
                        printer.showMessageLine("${UiMessages.FAILED_TO_ADD_STATE}${exception.message}")
                    }
                }
            } ?: showInvalidInput()
    }

    private fun showInvalidInput() {
        printer.showMessageLine(UiMessages.INVALID_INPUT)
        printer.showMessageLine(UiMessages.PLEASE_TRY_AGAIN)
    }

    fun updateState() {
        printer.showMessageLine(UiMessages.PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_UPDATE)
        val currentStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                showInvalidInput()
                return
            }

        printer.showMessageLine(UiMessages.PLEASE_ENTER_THE_NEW_STATE_NAME)
        val newStateName = reader.readStringOrNull()?.takeIf { it.isNotBlank() }
            ?: run {
                showInvalidInput()
                return
            }
        runBlocking {
            try {
                manageEntityStatesUseCase.updateEntityStateByName(
                    stateName = currentStateName,
                    newStateName = newStateName
                )
                val stateId = manageEntityStatesUseCase.getEntityStateIdByName(newStateName)
                auditServicesUseCase.addAuditForUpdateEntity(
                    entityType = EntityType.STATE,
                    existEntityName = currentStateName,
                    newEntityName = newStateName,
                    entityId = stateId
                )
                printer.showMessageLine(UiMessages.STATE_UPDATED_SUCCESSFULLY)
            } catch (exception: Exception) {
                printer.showMessageLine("${UiMessages.FAILED_TO_UPDATE_STATE} ${exception.message}")
            }
        }
    }

    fun deleteState() {
        printer.showMessageLine(UiMessages.PLEASE_ENTER_STATE_NAME_YOU_WANT_TO_DELETE)
        reader.readStringOrNull().takeIf { stateName ->
            stateName != null
        }?.let { stateName ->
            runBlocking {
                try {
                    val stateId = manageEntityStatesUseCase.getEntityStateIdByName(stateName)
                    manageEntityStatesUseCase.deleteEntityState(stateName = stateName)
                    auditServicesUseCase.addAuditForDeleteEntity(
                        entityType = EntityType.STATE,
                        entityName = stateName,
                        entityId = stateId,
                    )
                    printer.showMessageLine(UiMessages.STATE_DELETED_SUCCESSFULLY)
                } catch (exception: Exception) {
                    printer.showMessageLine("${UiMessages.FAILED_TO_DELETE_STATE} ${exception.message}")
                }
            }
        } ?: showInvalidInput()
    }
}

