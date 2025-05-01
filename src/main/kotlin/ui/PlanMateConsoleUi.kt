package org.example.ui

import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.features.login.LoginUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.StateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.AddUserUi


class PlanMateConsoleUi(
    private val loginUi: LoginUi,
    private val manageAuditSystemUi: AuditSystemManagerUiImp,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: StateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val addUserUi: AddUserUi,
) {

}