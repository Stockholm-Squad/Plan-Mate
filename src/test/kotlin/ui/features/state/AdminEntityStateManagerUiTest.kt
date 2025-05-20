package ui.features.state

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.example.logic.EntityStateAlreadyExistException
import org.example.logic.EntityStateNotDeletedException
import org.example.logic.NotAllowedEntityStateNameException
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AdminEntityStateManagerUiTest {
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase
    private lateinit var adminStateManagerUi: AdminEntityStateManagerUi
    private lateinit var showAllEntityStateManagerUi: ShowAllEntityStateManagerUi
    private lateinit var auditServicesUseCase: AuditServicesUseCase
    private lateinit var printer: OutputPrinter
    private lateinit var reader: InputReader

    @BeforeEach
    fun setUp() {
        printer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        showAllEntityStateManagerUi = mockk(relaxed = true)
        auditServicesUseCase = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)

        adminStateManagerUi = AdminEntityStateManagerUi(
            showAllEntityStateManagerUi = showAllEntityStateManagerUi,
            manageEntityStatesUseCase = manageStatesUseCase,
            reader = reader,
            printer = printer,
            auditServicesUseCase = auditServicesUseCase,
        )
    }

    @Test
    fun `UpdateState should succeed with valid input`() = runTest {
        every { reader.readStringOrNull() } returns "ExistingState"
        every { reader.readStringOrNull() } returns "NewState"

        coEvery {
            manageStatesUseCase.updateEntityStateByName(
                "ExistingState",
                "NewState"
            )
        } returns true

        adminStateManagerUi.updateState()

        verify { printer.showMessageLine("State updated successfully ^_^") }
    }

    @Test
    fun `UpdateState() should print state updated successfully when a valid name`() = runTest {
        //Given
        val stateName = "TODO"
        val newState = "Done"
        every { reader.readStringOrNull() } returns stateName
        coEvery {
            this@AdminEntityStateManagerUiTest.manageStatesUseCase.updateEntityStateByName(
                stateName,
                newState
            )
        } returns true

        //When
        adminStateManagerUi.updateState()

        //Then
        verify { printer.showMessageLine("Please enter the state you want to update: ") }
        verify { printer.showMessageLine("State updated successfully ^_^") }
    }

    @Test
    fun `UpdateState() should print invalid input when enter not valid name`() = runTest {
        val stateName = "1hnfjnj!"
        val newState = "Done"
        every { reader.readStringOrNull() } returns stateName
        every { reader.readStringOrNull() } returns newState

        coEvery { manageStatesUseCase.updateEntityStateByName(any(), any()) } throws
                NotAllowedEntityStateNameException()

        //When
        adminStateManagerUi.updateState()

        //Then
        verify { printer.showMessageLine("Failed to Update state: " + NotAllowedEntityStateNameException().message) }
    }

    @Test
    fun `UpdateState() should print INVALID_INPUT when enter null`() {
        // Given
        every { reader.readStringOrNull() } returns null

        //When
        adminStateManagerUi.updateState()

        //Then
        verify { printer.showMessageLine("Invalid input") }
    }

    @Test
    fun `UpdateState should fail with invalid input`() {
        every { reader.readStringOrNull() } returns ""

        adminStateManagerUi.updateState()

        verify { printer.showMessageLine("Invalid input") }
    }

    @Test
    fun `deleteState()  should show success message on valid input`() = runTest {
        every { reader.readStringOrNull() } returns "StateToDelete"
        coEvery { manageStatesUseCase.deleteEntityState("StateToDelete") } returns true

        adminStateManagerUi.deleteState()

        verify { printer.showMessageLine("State deleted successfully ^_^") }
    }

    @Test
    fun `deleteState() should show error message on failure`() = runTest {
        every { reader.readStringOrNull() } returns "BadState"
        coEvery { manageStatesUseCase.deleteEntityState("BadState") } throws Exception("some error")

        adminStateManagerUi.deleteState()

        verify { printer.showMessageLine("Failed to Delete state: some error") }
    }

    @Test
    fun `deleteState() should show invalid input when the input equal null`() {
        every { reader.readStringOrNull() } returns null

        adminStateManagerUi.deleteState()

        verify {
            printer.showMessageLine("Please enter the state you want to delete: ")
            printer.showMessageLine("Invalid input")
        }
    }


    @Test
    fun `deleteState() should print state deleted message when state is deleted`() = runTest {
        // Given
        val stateName = "TODO"
        every { reader.readStringOrNull() } returns stateName
        coEvery { manageStatesUseCase.deleteEntityState(any()) } returns true

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessageLine("Please enter the state you want to delete: ") }
        verify { printer.showMessageLine("State deleted successfully ^_^") }
    }

    @Test
    fun `deleteState() should print state not exist when use case returns state not exist`() = runTest {
        // Given
        val stateName = "In-Progress"
        every { reader.readStringOrNull() } returns stateName andThen "yes"
        coEvery { manageStatesUseCase.deleteEntityState(any()) } throws
                EntityStateNotDeletedException()

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessageLine("Failed to Delete state: " + EntityStateNotDeletedException().message) }
    }

    @Test
    fun `addState should succeed with valid input`() = runTest {
        every { reader.readStringOrNull() } returns "NewState"
        coEvery { manageStatesUseCase.addEntityState(any()) } returns true

        adminStateManagerUi.addState()

        verify { printer.showMessageLine("Please enter name for the state:") }
        verify { printer.showMessageLine("State added successfully ^_^") }
    }

    @Test
    fun `addState should fail with invalid input`() {
        every { reader.readStringOrNull() } returns null

        adminStateManagerUi.addState()

        verify { printer.showMessageLine("Invalid input") }
    }

    @Test
    fun `addState() should add success when enter valid state`() = runTest {
        //Given
        val stateName = "do"
        every { reader.readStringOrNull() } returns stateName
        coEvery { manageStatesUseCase.addEntityState(stateName) } returns true

        //When
        adminStateManagerUi.addState()

        //Then
        verify { printer.showMessageLine("Please enter name for the state:") }
        verify { printer.showMessageLine("State added successfully ^_^") }
    }

    @Test
    fun `addState() should print STATE_ALREADY_EXIST_MESSAGE when the use case return state already exist exception `() {
        // Given
        val stateName = "do"
        every { reader.readStringOrNull() } returns stateName
        coEvery { manageStatesUseCase.addEntityState(stateName) } throws
                EntityStateAlreadyExistException()
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessageLine("Failed to Add state: The state is already exist!") }

    }

    @Test
    fun `addState() should print NOT_ALLOWED_STATE_NAME_MESSAGE when the use case return not allowed state name exception`() =
        runTest {
            // Given
            val stateName = "1in review!"
            every { reader.readStringOrNull() } returns stateName
            coEvery { manageStatesUseCase.addEntityState(stateName) } throws EntityStateAlreadyExistException()
            //When
            adminStateManagerUi.addState()
            //Then
            verify { printer.showMessageLine("Failed to Add state: Only letters are allowed!") }
        }

    @Test
    fun `addState() should print STATE_NAME_LENGTH_MESSAGE failure result with not allowed length exception when the name of state is more than 30`() =
        runTest {
            // Given
            val stateName = "hi in this state this is too long state"
            every { reader.readStringOrNull() } returns stateName
            coEvery { manageStatesUseCase.addEntityState(stateName) } throws
                    EntityStateAlreadyExistException()
            //When
            adminStateManagerUi.addState()
            //Then
            verify { printer.showMessageLine("Failed to Add state: The state name is too long!") }
        }

    @Test
    fun `addState() should print INVALID_INPUT when the name of state is null`() {
        // Given
        every { reader.readStringOrNull() } returns null
        //When
        adminStateManagerUi.addState()
        //Then
        verify { printer.showMessageLine("Invalid input") }
    }
}