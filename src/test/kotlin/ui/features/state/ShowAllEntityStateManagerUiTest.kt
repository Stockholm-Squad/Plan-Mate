package ui.features.state

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.example.logic.NoEntityStateFoundException
import org.example.logic.entities.EntityState
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ShowAllEntityStateManagerUiTest {

    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase
    private lateinit var showAllEntityStateManagerUi: ShowAllEntityStateManagerUi
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        printer = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)
        showAllEntityStateManagerUi = ShowAllEntityStateManagerUi(manageStatesUseCase, printer)
    }

    @Test
    fun `showAllStates() should print no states exist when use case returns No state exist exception`() = runTest {
        //Given
        coEvery { manageStatesUseCase.getAllEntityStates() } throws NoEntityStateFoundException()

        //When
        showAllEntityStateManagerUi.launchUi()

        //Then
        verify { printer.showMessageLine("Failed to Load data, " + NoEntityStateFoundException().message) }
        verify { printer.showMessageLine("Please try again ^_^") }

    }

    @Test
    fun `showAllStates() should print states when use case returns states`() = runTest {
        //Given
        val entityStates = listOf(
            EntityState(id = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa1"), title = "TODO"),
            EntityState(id = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa4"), title = "TODO"),
        )
        coEvery { manageStatesUseCase.getAllEntityStates() } returns entityStates

        //When
        showAllEntityStateManagerUi.launchUi()

        //Then
        verify(exactly = 1) { printer.showStates(entityStates) }
    }
}