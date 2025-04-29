package org.example.ui

import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.StateManagerUi


class PlanMateConsoleUi(
    private val manageAuthenticationUi: AuthenticationManagerUi,
    private val manageAuditSystemUi: AuditSystemManagerUi,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: StateManagerUi,
) {

}