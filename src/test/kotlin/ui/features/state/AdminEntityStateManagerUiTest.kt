package ui.features.state

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.EntityStateAlreadyExistException
import org.example.logic.EntityStateNotDeletedException
import org.example.logic.NotAllowedEntityStateNameException
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

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
    fun `UpdateState() should print state updated successfully when a valid name`() = runTest {
        //Given
        val stateName = "TODO"
        val newState = "Done"
        every { reader.readStringOrNull() } returns stateName andThen newState
        coEvery {
            this@AdminEntityStateManagerUiTest.manageStatesUseCase.updateEntityStateByName(
                stateName,
                newState
            )
        } returns true
        coEvery { manageStatesUseCase.getEntityStateIdByName(stateName) } returns UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        coEvery { auditServicesUseCase.addAuditForUpdateEntity(any(), any(), any(), any()) } just runs

        //When
        adminStateManagerUi.updateState()

        //Then
        verify { printer.showMessageLine(UiMessages.STATE_UPDATED_SUCCESSFULLY) }
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
        verify { printer.showMessageLine("${UiMessages.FAILED_TO_UPDATE_STATE} ${NotAllowedEntityStateNameException().message}") }
    }

    @Test
    fun `UpdateState() should print INVALID_INPUT when enter null`() {
        // Given
        every { reader.readStringOrNull() } returns null

        //When
        adminStateManagerUi.updateState()

        //Then
        verify { printer.showMessageLine(UiMessages.INVALID_INPUT) }
    }

    @Test
    fun `UpdateState should fail with invalid input`() {
        //Given
        every { reader.readStringOrNull() } returns ""

        //When
        adminStateManagerUi.updateState()

        //Then
        verify { printer.showMessageLine(UiMessages.INVALID_INPUT) }
    }

    @Test
    fun `deleteState() should show invalid input when the input equal null`() {
        //Given
        every { reader.readStringOrNull() } returns null

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessageLine(UiMessages.INVALID_INPUT) }
    }


    @Test
    fun `deleteState() should print state deleted message when state is deleted`() = runTest {
        // Given
        val stateName = "TODO"
        every { reader.readStringOrNull() } returns stateName
        coEvery { manageStatesUseCase.getEntityStateIdByName(stateName) } returns UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        coEvery { manageStatesUseCase.deleteEntityState(any()) } returns true
        coEvery { auditServicesUseCase.addAuditForDeleteEntity(any(), any(), any()) } just runs

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessageLine(UiMessages.STATE_DELETED_SUCCESSFULLY) }
    }

    @Test
    fun `deleteState() should print state not exist when use case returns state not exist`() = runTest {
        // Given
        val stateName = "In-Progress"
        every { reader.readStringOrNull() } returns stateName
        coEvery { manageStatesUseCase.getEntityStateIdByName(stateName) } returns UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        coEvery { manageStatesUseCase.deleteEntityState(any()) } throws
                EntityStateNotDeletedException()

        //When
        adminStateManagerUi.deleteState()

        //Then
        verify { printer.showMessageLine("${ UiMessages.FAILED_TO_DELETE_STATE} ${EntityStateNotDeletedException().message}") }
    }

    @Test
    fun `addState() should add success when enter valid state`() = runTest {
        //Given
        val stateName = "do"
        every { reader.readStringOrNull() } returns stateName
        coEvery { manageStatesUseCase.addEntityState(stateName) } returns true
        coEvery { manageStatesUseCase.getEntityStateIdByName(stateName) } returns UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        coEvery { auditServicesUseCase.addAuditForAddEntity(any(), any(), any()) } just runs

        //When
        adminStateManagerUi.addState()

        //Then
        verify { printer.showMessageLine(UiMessages.STATE_ADDED_SUCCESSFULLY) }
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
        verify { printer.showMessageLine("${UiMessages.FAILED_TO_ADD_STATE}${EntityStateAlreadyExistException().message}") }

    }

    @Test
    fun `addState() should print NOT_ALLOWED_STATE_NAME_MESSAGE when the use case return not allowed state name exception`() =
        runTest {
            // Given
            val stateName = "1in review!"
            every { reader.readStringOrNull() } returns stateName
            coEvery { manageStatesUseCase.addEntityState(stateName) } throws NotAllowedEntityStateNameException()
            //When
            adminStateManagerUi.addState()
            //Then
            verify { printer.showMessageLine("${UiMessages.FAILED_TO_ADD_STATE}${NotAllowedEntityStateNameException().message}") }
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
            verify { printer.showMessageLine("${UiMessages.FAILED_TO_ADD_STATE}${EntityStateAlreadyExistException().message}") }
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