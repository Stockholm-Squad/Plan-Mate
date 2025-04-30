package ui.features.state

import createState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.State
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinterImplementation
import org.example.logic.model.exceptions.ExceptionMessage
import org.example.logic.model.exceptions.PlanMateExceptions.DataException
import org.example.logic.model.exceptions.PlanMateExceptions.LogicException
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.ui.features.common.state.UserStateManagerUi
import org.example.ui.features.state.AdminStateManagerUiImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AdminStateManagerUiImplTest {
    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase
    private lateinit var adminStateManagerUi: AdminStateManagerUiImpl
    private lateinit var userStateManagerUi: UserStateManagerUi
    private lateinit var printer: OutputPrinterImplementation
    private lateinit var reader: InputReader

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        userStateManagerUi = mockk(relaxed = true)

        manageStatesUseCase = ManageStatesUseCase(stateRepository)
        adminStateManagerUi = AdminStateManagerUiImpl(
            userStateManagerUi = userStateManagerUi,
            manageStatesUseCase = manageStatesUseCase,
            inputReader = reader,
            outputPrinter = printer
        )
    }

    @Test
    fun `editState() should print state updated successfully when a valid name`() {
        val stateName = "TODO"

        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.editState(any()) } returns Result.success(true)

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage("State Updated successfully") }
    }

    @Test
    fun `editState() should print not allowed state name  when enter not valid name`() {
        val stateName = "1hnfjnj!"

        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.editState(any()) } returns Result.failure(
            LogicException.NotAllowedStateNameException()
        )

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage(ExceptionMessage.NOT_ALLOWED_STATE_NAME_MESSAGE.message) }
    }

    @Test
    fun `editState() should print state name is too long when enter not valid name`() {
        val stateName = "hi in this state this is too long state"

        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.editState(stateName) } returns Result.failure(
            LogicException.StateNameLengthException()
        )

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage(ExceptionMessage.STATE_NAME_LENGTH_MESSAGE.message) }
    }

    @Test
    fun `editState() should print INVALID_INPUT when enter null`() {
        // Given
        every { reader.readStringOrNull() } returns null

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage(ExceptionMessage.INVALID_INPUT.message) }
    }

    @Test
    fun `editState() should print error happens when edit use case failed`() {
        val stateName = "TODO"

        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.editState(any()) } returns Result.failure(
            Throwable()
        )

        //When
        adminStateManagerUi.editState()

        //Then
        verify { printer.showMessage("Failed to edit state, please try again!") }
    }

    @Test
    fun `deleteState() should print state deleted message when state exist, user confirm to delete and state deleted`() {
        // Given
        val stateName = "TODO"
        every { reader.readStringOrNull() } returns stateName andThen "yes"
        every { manageStatesUseCase.deleteState(any()) } returns Result.success(true)

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessage("State deleted successfully") }
    }

    @Test
    fun `deleteState() should print state not exist when state not exist`() {
        // Given
        val stateName = "In-Progress"
        every { reader.readStringOrNull() } returns stateName andThen "yes"
        every { manageStatesUseCase.deleteState(any()) } returns Result.failure(
            LogicException.StateNotExistException()
        )

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessage(ExceptionMessage.STATE_NOT_EXIST_MESSAGE.message) }
    }

    @Test
    fun `deleteState() should print failed to delete the state when delete failed`() {
        // Given
        val stateName = "In-Progress"
        every { reader.readStringOrNull() } returns stateName andThen "yes"
        every { manageStatesUseCase.deleteState(any()) } returns Result.failure(
            Throwable()
        )

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessage("Failed to delete the state") }
    }

    @Test
    fun `deleteState() should print okay, as you like when use not confirm to delete`() {
        // Given
        val stateName = "In-Progress"
        every { reader.readStringOrNull() } returns stateName andThen "No"

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessage("Okay, As you like!") }
    }


    @Test
    fun `addState() should add success when enter valid state`() {

        val stateName = "do"

        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addState(State(name = stateName)) } returns Result.success(true)
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage("add successfully") }
    }

    @Test
    fun `addState() should print STATE_ALREADY_EXIST_MESSAGE when the use case return state already exist exception `() {
        // Given
        val stateName = "do"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addState(State(name = stateName)) } returns Result.failure(
            LogicException.StateAlreadyExistException(
                ExceptionMessage.STATE_ALREADY_EXIST_MESSAGE
            )
        )
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage(ExceptionMessage.STATE_ALREADY_EXIST_MESSAGE.message) }

    }

    @Test
    fun `addState() should print NOT_ALLOWED_STATE_NAME_MESSAGE when the use case return not allowed state name exception`() {
        // Given
        val stateName = "1in review!"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addState(State(name = stateName)) } returns Result.failure(
            LogicException.StateAlreadyExistException(
                ExceptionMessage.NOT_ALLOWED_STATE_NAME_MESSAGE
            )
        )
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage(ExceptionMessage.NOT_ALLOWED_STATE_NAME_MESSAGE.message) }
    }

    @Test
    fun `addState() should print STATE_NAME_LENGTH_MESSAGE failure result with not allowed length exception when the name of state is more than 30`() {
        // Given
        val stateName = "hi in this state this is too long state"
        every { reader.readStringOrNull() } returns stateName
        every { manageStatesUseCase.addState(State(name = stateName)) } returns Result.failure(
            LogicException.StateAlreadyExistException(
                ExceptionMessage.STATE_NAME_LENGTH_MESSAGE
            )
        )
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage(ExceptionMessage.STATE_NAME_LENGTH_MESSAGE.message) }
    }

    @Test
    fun `addState() should print INVALID_INPUT when the name of state is null`() {
        // Given
        every { reader.readStringOrNull() } returns null
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessage(ExceptionMessage.INVALID_INPUT.message) }
    }

    @Test
    fun `showAllStates() should print all states when have data`() {
        // Given
        val state = listOf(
            createState("2", "done"),
            (createState("3", "in review"))
        )
        every { manageStatesUseCase.getAllStates() } returns Result.success(state)
//        every { stateRepository.addState(state)} returns Result.success(true)

        //When
        adminStateManagerUi.showAllStates()

        //Then
        verify { printer.showMessage(state.toString()) } // this change when i implement in ui

    }

    @Test
    fun `showAllStates() should print empty data message when no data available`() {
        // Given
        every { manageStatesUseCase.getAllStates() } returns Result.failure(DataException.EmptyDataException())
        //When
        adminStateManagerUi.showAllStates()

        //Then
        verify { printer.showMessage(ExceptionMessage.EMPTY_DATA_MESSAGE.message) }

    }


}