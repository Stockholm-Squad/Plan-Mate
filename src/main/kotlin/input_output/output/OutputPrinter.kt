package org.example.input_output.output

import logic.model.entities.State
import logic.model.entities.AuditSystem

interface OutputPrinter {
    fun showMessage(message: String)
    fun showStates(states: List<State>)
    fun showAudits(audits: List<AuditSystem>)
}