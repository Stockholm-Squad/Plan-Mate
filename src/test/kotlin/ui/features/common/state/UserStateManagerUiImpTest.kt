package ui.features.common.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.State
import org.example.input_output.output.OutputPrinter
import org.example.logic.model.exceptions.ExceptionMessage
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUi
import org.example.ui.features.common.state.UserStateManagerUiImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserStateManagerUiImpTest {

    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase
    private lateinit var userStateManagerUi: UserStateManagerUi
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(stateRepository)
        userStateManagerUi = UserStateManagerUiImp(manageStatesUseCase, printer)
    }

    @Test
    fun `showAllStates() should print no states exist when use case returns No state exist exception`() {
        //Given
        every { manageStatesUseCase.getAllStates() } returns Result.failure(
            PlanMateExceptions.LogicException.NoStatesFoundedException()
        )

        //When
        userStateManagerUi.showAllStates()

        //Then
        verify { printer.showMessage(ExceptionMessage.NO_STATE_FOUNDED_MESSAGE.message) }

    }

    @Test
    fun `showAllStates() should print states when use case returns states`() {
        //Given
        val states = listOf(
            State(id = "12", name = "TODO"),
            State(id = "12", name = "TODO"),
        )
        every { manageStatesUseCase.getAllStates() } returns Result.success(states)

        //When
        userStateManagerUi.showAllStates()

        //Then
        verify { printer.showState(states[0]) }
        verify { printer.showState(states[1]) }
    }
}