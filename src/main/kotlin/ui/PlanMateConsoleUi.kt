package org.example.ui

import org.example.ui.features.GetAuditSystemUi
import org.example.ui.features.GetAuthenticationUi
import org.example.ui.features.GetProjectUi
import org.example.ui.features.GetStateUi
import org.example.ui.features.GetTaskUi

class PlanMateConsoleUi(
    private val getAuthenticationUi: GetAuthenticationUi,
    private val getAuditSystemUi: GetAuditSystemUi,
    private val getTaskUi: GetTaskUi,
    private val getStateUi: GetStateUi,
    private val getProjectUi: GetProjectUi
) {


}