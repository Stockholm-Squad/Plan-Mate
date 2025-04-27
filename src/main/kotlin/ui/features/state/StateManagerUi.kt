package org.example.ui.features.state

import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.AdminStateManagerUi
import org.example.ui.features.common.UserStateManagerUi

class StateManagerUi(
    private val getStateUseCase: ManageStatesUseCase
) : AdminStateManagerUi, UserStateManagerUi {
}