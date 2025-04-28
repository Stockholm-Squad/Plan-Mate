package org.example.ui.features.audit

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.PlanMateConsoleUi

class AuditSystemManagerUi(
    private val getAuditSystemUseCase: ManageAuditSystemUseCase,
    private val printer: OutputPrinter,
    private val reader: InputReader,
    private val auditSystemPlayer: PlanMateConsoleUi = PlanMateConsoleUi(
        manageAuthenticationUi = TODO(),
        manageAuditSystemUi = TODO(),
        manageProjectUi = TODO(),
        stateManagerUi = TODO(),
        taskManagerUi = TODO()
    ),

    ) {


}