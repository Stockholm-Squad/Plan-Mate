package org.example.ui.features.state

import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.admin.AdminEntityStateManagerUi
import org.example.ui.features.state.mate.MateEntityStateManagerUi
import org.example.ui.input_output.output.OutputPrinter

class EntityEntityStateManagerUiImp(
    private val adminStateManagerUi: AdminEntityStateManagerUi,
    private val mateStateManagerUi: MateEntityStateManagerUi,
    private val printer: OutputPrinter
) : EntityStateManageUi {
    override fun launchStateManagerUi(user: User?) {
        when (user?.userRole) {
            UserRole.ADMIN -> adminStateManagerUi.launchUi(user)
            UserRole.MATE -> mateStateManagerUi.launchUi(user)
            else -> printer.showMessage(UiMessages.INVALID_USER)
        }
    }

    override fun launchUi(user: User?) {
        launchStateManagerUi(user = user)
    }
}