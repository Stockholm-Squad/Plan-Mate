package org.example.input_output.output

import logic.model.entities.AuditSystem


interface OutputPrinter {
    fun showMessage(message: String)
    fun showAudits(audits: List<AuditSystem>)
}