package org.example.ui.features.audit

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType
import org.example.data.repo.AuditSystemRepositoryImp
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.ui.PlanMateConsoleUi
import org.example.utils.Constant
import org.example.utils.SearchUtils

class AuditSystemManagerUi(
    private val getAuditSystemUseCase: ManageAuditSystemUseCase,
    private val auditSystemRepositoryImp: AuditSystemRepositoryImp,
    private val searchUtils: SearchUtils,
    private val printer: OutputPrinter,
    private val reader: InputReader,

    ) {


}