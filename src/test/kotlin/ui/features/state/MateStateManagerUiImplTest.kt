package ui.features.state

import io.mockk.mockk
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.MateStateManagerUi
import org.example.ui.features.state.MateStateManagerUiImpl
import org.junit.jupiter.api.BeforeEach

class MateStateManagerUiImplTest {
    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase
    private lateinit var mateStateManagerUi: MateStateManagerUi

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(stateRepository)
        mateStateManagerUi = MateStateManagerUiImpl(manageStatesUseCase)

    }
}