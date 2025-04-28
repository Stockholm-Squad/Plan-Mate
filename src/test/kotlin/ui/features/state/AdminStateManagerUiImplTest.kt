package ui.features.state

import io.mockk.mockk
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.AdminStateManagerUi
import org.example.ui.features.state.AdminStateManagerUiImpl
import org.junit.jupiter.api.BeforeEach

class AdminStateManagerUiImplTest {
    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase
    private lateinit var adminStateManagerUi: AdminStateManagerUi

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(stateRepository)
        adminStateManagerUi = AdminStateManagerUiImpl(manageStatesUseCase)
    }
}