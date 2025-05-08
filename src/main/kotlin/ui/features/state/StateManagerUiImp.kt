package org.example.ui.features.state

import kotlinx.coroutines.runBlocking
import logic.models.entities.User
import logic.models.entities.UserRole
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi


class StateManagerUiImp(
    private val adminStateManagerUi: AdminStateManagerUi,
    private val mateStateManagerUi: MateStateManagerUi,
    private val printer: OutputPrinter
) : StateManageUi {
    override fun launchStateManagerUi(user: User?) {
        runBlocking {
            when (user?.userRole) {
                UserRole.ADMIN -> adminStateManagerUi.launchUi(user)
                UserRole.MATE -> mateStateManagerUi.launchUi(user)
                else -> printer.showMessage(UiMessages.INVALID_USER)
            }
        }
    }

    override suspend fun launchUi(user: User?) {
        launchStateManagerUi(user = user)
    }
}