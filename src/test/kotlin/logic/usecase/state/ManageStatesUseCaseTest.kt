package logic.usecase.state

import io.mockk.mockk
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.junit.jupiter.api.BeforeEach

class ManageStatesUseCaseTest {

    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(stateRepository)
    }
}