package org.example.ui

import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.StateManagerUi
import org.example.ui.features.task.TaskManagerUi


class PlanMateConsoleUi(
    private val manageAuthenticationUi: AuthenticationManagerUi,
    private val manageAuditSystemUi: AuditSystemManagerUiImp,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: StateManagerUi,
    private val taskManagerUi: TaskManagerUi
) {

}