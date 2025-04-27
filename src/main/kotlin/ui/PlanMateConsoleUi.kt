package org.example.ui

import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.common.AdminStateManagerUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.task.TaskManagerUi


class PlanMateConsoleUi(
    private val manageAuthenticationUi: AuthenticationManagerUi,
    private val manageAuditSystemUi: AuditSystemManagerUi,
    private val manageProjectUi: ProjectManagerUi,
    private val stateManagerUi: AdminStateManagerUi,
    private val taskManagerUi: TaskManagerUi
) {

}