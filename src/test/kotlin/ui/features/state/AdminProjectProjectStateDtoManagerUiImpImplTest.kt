package ui.features.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.models.exceptions.NotAllowedStateNameException
import logic.models.exceptions.StateAlreadyExistException
import logic.models.exceptions.StateNotExistException
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.state.admin.AdminStateManagerUiImpl
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AdminProjectProjectStateDtoManagerUiImpImplTest {
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase
    private lateinit var adminStateManagerUi: AdminStateManagerUiImpl
    private lateinit var userStateManagerUi: UserStateManagerUi
    private lateinit var printer: OutputPrinter
    private lateinit var reader: InputReader

    @BeforeEach
    fun setUp() {
        printer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        userStateManagerUi = mockk(relaxed = true)

        manageStatesUseCase = mockk(relaxed = true)
        adminStateManagerUi = AdminStateManagerUiImpl(
            userStateManagerUi = userStateManagerUi,
            manageStatesUseCase = manageStatesUseCase,
            reader = reader,
            printer = printer
        )
    }

    @Test
    fun `editState should succeed with valid input`() {
        every { reader.readStringOrNull() } returns "ExistingState"
        every { reader.readStringOrNull() } returns "NewState"

        every { manageStatesUseCase.editEntityStateByName("ExistingState", "NewState") } returns Result.success(true)

        adminStateManagerUi.editState()

        verify { printer.showMessage("State updated successfully ^_^") }
    }
    @Test
    fun `editState() should print state updated successfully when a valid name`() {
        //Given
        val stateName = "TODO"
        val newState ="Done"
        every { reader.readStringOrNull() } returns stateName
        every {
            this@AdminProjectProjectStateDtoManagerUiImpImplTest.manageStatesUseCase.editEntityStateByName(
                stateName,
                newState
            )
        } returns Result.success(true)

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage("Please enter the state you want to update: ") }
        verify { printer.showMessage("State updated successfully ^_^") }
    }

    @Test
    fun `editState() should print invalid input when enter not valid name`() {
        val stateName = "1hnfjnj!"
        val newState ="Done"
        every { reader.readStringOrNull() } returns stateName
        every { reader.readStringOrNull() } returns newState

        every { manageStatesUseCase.editEntityStateByName(any(), any()) } returns Result.failure(
           NotAllowedStateNameException()
        )

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage("Failed to Update state: " + NotAllowedStateNameException().message) }
    }

    @Test
    fun `editState() should print INVALID_INPUT when enter null`() {
        // Given
        every { reader.readStringOrNull() } returns null

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage("Invalid input") }
    }

    @Test
    fun `editState should fail with invalid input`() {
        every { reader.readStringOrNull() } returns ""

        adminStateManagerUi.editState()

        verify { printer.showMessage("Invalid input") }
    }

    @Test
    fun `deleteState()  should show success message on valid input`() {
        every { reader.readStringOrNull() } returns "StateToDelete"
        every { manageStatesUseCase.deleteEntityState("StateToDelete") } returns Result.success(true)

        adminStateManagerUi.deleteState()

        verify { printer.showMessage("State deleted successfully ^_^") }
    }

    @Test
    fun `deleteState() should show error message on failure`() {
        every { reader.readStringOrNull() } returns "BadState"
        every { manageStatesUseCase.deleteEntityState("BadState") } returns Result.failure(Exception("some error"))

        adminStateManagerUi.deleteState()

        verify { printer.showMessage("Failed to Delete state: some error") }
    }
    @Test
    fun `deleteState() should show invalid input when the input equal null`() {
        every { reader.readStringOrNull() } returns null

        adminStateManagerUi.deleteState()

        verify {         printer.showMessage("Please enter the state you want to delete: ")
            printer.showMessage("Invalid input") }
    }


    @Test
    fun `deleteState() should print state deleted message when state is deleted`() {
        // Given
        val stateName = "TODO"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.deleteEntityState(any()) } returns Result.success(true)

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessage("Please enter the state you want to delete: ") }
        verify { printer.showMessage("State deleted successfully ^_^") }
    }

    @Test
    fun `deleteState() should print state not exist when use case returns state not exist`() {
        // Given
        val stateName = "In-Progress"
        every { reader.readStringOrNull() } returns stateName andThen "yes"
        every { manageStatesUseCase.deleteEntityState(any()) } returns Result.failure(
            StateNotExistException()
        )

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessage("Failed to Delete state: " + StateNotExistException().message) }
    }
    @Test
    fun `addState should succeed with valid input`() {
        every { reader.readStringOrNull() } returns "NewState"
        every { manageStatesUseCase.addEntityState(any()) } returns Result.success(true)

        adminStateManagerUi.addState()

        verify { printer.showMessage("Please enter name for the state:") }
        verify { printer.showMessage("State added successfully ^_^") }
    }

    @Test
    fun `addState should fail with invalid input`() {
        every { reader.readStringOrNull() } returns null

        adminStateManagerUi.addState()

        verify { printer.showMessage("Invalid input") }
    }
    @Test
    fun `addState() should add success when enter valid state`() {
        //Given
        val stateName = "do"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addEntityState(stateName) } returns Result.success(true)

        //When
        adminStateManagerUi.addState()

        //Then
        verify { printer.showMessage("Please enter name for the state:") }
        verify { printer.showMessage("State added successfully ^_^") }
    }

    @Test
    fun `addState() should print STATE_ALREADY_EXIST_MESSAGE when the use case return state already exist exception `() {
        // Given
        val stateName = "do"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addEntityState(stateName) } returns Result.failure(
          StateAlreadyExistException()
        )
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage("Failed to Add state: The state is already exist!") }

    }

    @Test
    fun `addState() should print NOT_ALLOWED_STATE_NAME_MESSAGE when the use case return not allowed state name exception`() {
        // Given
        val stateName = "1in review!"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addEntityState(stateName) } returns Result.failure(
            StateAlreadyExistException()
        )
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage("Failed to Add state: Only letters are allowed!") }
    }

    @Test
    fun `addState() should print STATE_NAME_LENGTH_MESSAGE failure result with not allowed length exception when the name of state is more than 30`() {
        // Given
        val stateName = "hi in this state this is too long state"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addEntityState(stateName) } returns Result.failure(
            StateAlreadyExistException()
        )
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage("Failed to Add state: The state name is too long!") }
    }

    @Test
    fun `addState() should print INVALID_INPUT when the name of state is null`() {
        // Given
        every { reader.readStringOrNull() } returns null
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage("Invalid input") }
    }

    @Test
    fun `showAllStates() should print empty data message when no data available`() {
        // Given & When
        adminStateManagerUi.showAllStates()

        //Then
        verify { userStateManagerUi.showAllStates() }
    }
    @Test
    fun `showAllStates should delegate to userStateManagerUi`() {
        // Given & When
        adminStateManagerUi.showAllStates()
        // Then
        verify { userStateManagerUi.showAllStates() }
    }


//    @ParameterizedTest
//    @CsvSource(
//        "1", "2", "3", "4",
//    )
//
//    fun `launchUi() should show option when call `(option: Int) {
//        // Given
//        every { reader.readIntOrNull() } returns option andThen 5
//        every { manageStatesUseCase.addProjectState(any()) } returns Result.success(true)
//        every { manageStatesUseCase.deleteProjectState(any()) } returns Result.success(true)
//        // When
//        adminStateManagerUi.launchUi()
//        // Then
//        verify {
//            printer.showMessage("What do you need ^_^")
//            StateMenuChoice.entries.forEach { item ->
//                printer.showMessage("${item.choiceNumber} ${item.choiceMessage}")
//
//                when (option) {
//                    1 -> adminStateManagerUi.showAllStates()
//                    2 -> adminStateManagerUi.addState()
//                    3 -> adminStateManagerUi.editState()
//                    4 -> adminStateManagerUi.deleteState()
//                    5 -> true
//                }
//            }
//        }
//    }
//    @Test
//    fun `launchUi() should print Please enter a valid choice when give invalid choice `() {
//        // Given
//        every { reader.readIntOrNull() } returns 7 andThen 5
//        // When
//        adminStateManagerUi.launchUi()
//        // Then
//        verify {
//            printer.showMessage("What do you need ^_^")
//            StateMenuChoice.entries.forEach { item ->
//                printer.showMessage("${item.choiceNumber} ${item.choiceMessage}")
//            }
//            printer.showMessage("Please enter a valid choice!!")
//        }
//    }
//    @Test
//    fun `launchUi() should print Please enter a valid choice!! when enter input equal null `() {
//        // Given
//        every { reader.readIntOrNull() } returns null andThen 5
//        // When
//        adminStateManagerUi.launchUi()
//        // Then
//        verify {
//            printer.showMessage("What do you need ^_^")
//            StateMenuChoice.entries.forEach { item ->
//                printer.showMessage("${item.choiceNumber} ${item.choiceMessage}")
//            }
//            printer.showMessage("Please enter a valid choice!!")
//        }
//    }

}