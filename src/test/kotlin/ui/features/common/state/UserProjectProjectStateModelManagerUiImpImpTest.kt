//package ui.features.common.state
//
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import logic.model.entities.ProjectState
//import org.example.ui.input_output.output.OutputPrinter
//import org.example.logic.model.exceptions.NoStatesFoundedException
//import org.example.logic.usecase.state.ManageStatesUseCase
//import org.example.ui.features.state.common.UserStateManagerUi
//import org.example.ui.features.state.common.UserStateManagerUiImp
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class UserProjectStateManagerUiImpImpTest {
//
//    private lateinit var manageStatesUseCase: ManageStatesUseCase
//    private lateinit var userStateManagerUi: UserStateManagerUi
//    private lateinit var printer: OutputPrinter
//
//    @BeforeEach
//    fun setUp() {
//        printer = mockk(relaxed = true)
//        manageStatesUseCase = mockk(relaxed = true)
//        userStateManagerUi = UserStateManagerUiImp(manageStatesUseCase, printer)
//    }
//
//    @Test
//    fun `showAllStates() should print no states exist when use case returns No state exist exception`() {
//        //Given
//        every { manageStatesUseCase.getAllProjectStates() } returns Result.failure(
//            NoStatesFoundedException()
//        )
//
//        //When
//        userStateManagerUi.showAllStates()
//
//        //Then
//        verify { printer.showMessage("Failed to Load data, " + NoStatesFoundedException().message) }
//        verify { printer.showMessage("Please try again ^_^") }
//
//    }
//
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
//}