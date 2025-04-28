package ui.features.common.state

import io.mockk.mockk
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUi
import org.example.ui.features.common.state.UserStateManagerUiImp
import org.junit.jupiter.api.BeforeEach

class UserStateManagerUiImpTest {

    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase
    private lateinit var userStateManagerUi: UserStateManagerUi

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(stateRepository)
        userStateManagerUi = UserStateManagerUiImp(manageStatesUseCase)
    }
}