package org.example.ui.features.project

import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.common.ui_launcher.UiLauncher
import org.example.ui.features.state.AdminStateManagerUi
import org.example.ui.features.state.StateManagerUi
import org.example.ui.features.task.TaskManagerUi

class ProjectManagerUi(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val stateManagerUi: AdminStateManagerUi,
    private val taskManagerUi: TaskManagerUi,
    private val authenticationManagerUi: AuthenticationManagerUi,
) : UiLauncher{
    fun showAllProjects() {
//        TODO("Not implemented tet")
    }

    fun showProjectById(id: String) {
        TODO("Not implemented tet")
    }

    fun addProject() {
        TODO("Not implemented tet")
    }

    fun editProject(id: String) {
        TODO("Not implemented tet")
    }

    fun deleteProject(id: String) {
        TODO("Not implemented tet")
    }

    fun assignUsersToProject() {
        TODO("Not implemented tet")
    }

    override fun launchUi() {
        TODO("Not yet implemented")
    }
}