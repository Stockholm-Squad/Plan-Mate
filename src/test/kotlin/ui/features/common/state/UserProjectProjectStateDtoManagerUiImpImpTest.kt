package ui.features.common.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.models.exceptions.NoStatesFoundedException
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.state.common.UserEntityStateManagerUi
import org.example.ui.features.state.common.UserEntityStateManagerUiImp
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserProjectProjectStateDtoManagerUiImpImpTest {

    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase
    private lateinit var userStateManagerUi: UserEntityStateManagerUi
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        printer = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)
        userStateManagerUi = UserEntityStateManagerUiImp(manageStatesUseCase, printer)
    }

    @Test
    fun `showAllStates() should print no states exist when use case returns No state exist exception`() {
        //Given
        every { manageStatesUseCase.getAllEntityStates() } returns Result.failure(
            NoStatesFoundedException()
        )

        //When
        userStateManagerUi.showAllStates()

        //Then
        verify { printer.showMessageLine("Failed to Load data, " + NoStatesFoundedException().message) }
        verify { printer.showMessageLine("Please try again ^_^") }

    }

//    @Test
//    fun `showAllStates() should print states when use case returns states`() {
//        //Given
//        val projectStates = listOf(
//            ProjectState(id = "12", name = "TODO"),
//            ProjectState(id = "12", name = "TODO"),
//        )
//        every { manageStatesUseCase.getAllProjectStates() } returns Result.success(projectStates)
//
//        //When
//        userStateManagerUi.showAllStates()
//
//        //Then
//        verify(exactly = 1) { printer.showStates(projectStates) }
//    }
}