package org.example.ui

import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.features.authentication.AuthenticateUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.StateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.AddUserUi


class PlanMateConsoleUi(
    private val authenticateUi: AuthenticateUi,
    private val manageAuditSystemUi: AuditSystemManagerUiImp,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: StateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val addUserUi: AddUserUi
) {

}