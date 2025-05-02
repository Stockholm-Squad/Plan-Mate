package ui.features.common.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.State
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.model.exceptions.NoStatesFoundedException
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.common.UserStateManagerUiImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserStateManagerUiImpTest {

    private lateinit var manageStatesUseCase: ManageStatesUseCase
    private lateinit var userStateManagerUi: UserStateManagerUi
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        printer = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)
        userStateManagerUi = UserStateManagerUiImp(manageStatesUseCase, printer)
    }

    @Test
    fun `showAllStates() should print no states exist when use case returns No state exist exception`() {
        //Given
        every { manageStatesUseCase.getAllStates() } returns Result.failure(
            NoStatesFoundedException()
        )

        //When
        userStateManagerUi.showAllStates()

        //Then
        verify { printer.showMessage("Failed to Load data, " + NoStatesFoundedException().message) }
        verify { printer.showMessage("Please try again ^_^") }

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
        verify(exactly = 1) { printer.showStates(states) }
    }
}