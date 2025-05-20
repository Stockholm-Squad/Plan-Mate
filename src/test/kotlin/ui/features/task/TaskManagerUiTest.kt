package ui.features.task


import io.mockk.*
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.common.utils.UiUtils
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TaskManagerUiTest {

    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var uiUtils: UiUtils
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var manageStateUseCase: ManageEntityStatesUseCase
    private lateinit var getProjectUseCase: GetProjectsUseCase
    private lateinit var taskManagerUi: TaskManagerUi
    private lateinit var auditServicesUseCase: AuditServicesUseCase
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        uiUtils = mockk(relaxed = true)
        getProjectUseCase = mockk(relaxed = true)
        manageStateUseCase = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        auditServicesUseCase = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)

        taskManagerUi = TaskManagerUi(
            reader,
            printer,
            uiUtils,
            manageTasksUseCase,
            manageStateUseCase,
            getProjectUseCase,
            auditServicesUseCase, loginUseCase
        )
    }

    //region createTask
    @Test
    fun `createTask() should create task when valid input is provided`() {
        // Given
        val name = "New Task"
        val description = "A new task"
        val stateName = "TODO"
        val stateId = UUID.randomUUID()

        every { reader.readStringOrNull() } returns name andThen description andThen stateName
        coEvery { manageStateUseCase.getEntityStateIdByName(stateName) } returns stateId
        coEvery { manageTasksUseCase.addTask(any(), any()) } returns true

        // When
        taskManagerUi.addTask("")

        // Then
        coVerify(exactly = 1) { manageStateUseCase.getEntityStateIdByName(stateName) }
        coVerify(exactly = 1) { manageTasksUseCase.addTask(any(), any()) }
        verify(exactly = 1) { printer.printTask(any()) }
    }

    @Test
    fun `createTask() should show error when name is empty`() = runTest {
        // Given
        every { reader.readStringOrNull() } returns "" // Empty name

        // When
        taskManagerUi.addTask("")

        // Then
        verify(exactly = 1) { printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT) }
        coVerify(exactly = 0) { manageStateUseCase.getEntityStateIdByName(any()) }
        coVerify(exactly = 0) { manageTasksUseCase.addTask(any(), any()) }
    }

    @Test
    fun `createTask() should show error when state name is empty`() = runTest {
        // Given
        val name = "New Task"
        val description = "A new task"
        every { reader.readStringOrNull() } returns name andThen description andThen ""

        // When
        taskManagerUi.addTask("")

        // Then
        verify(exactly = 1) { printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT) }
        coVerify(exactly = 0) { manageStateUseCase.getEntityStateIdByName(any()) }
        coVerify(exactly = 0) { manageTasksUseCase.addTask(any(), any()) }
    }

    @Test
    fun `createTask() should show error when state name is invalid`() = runTest {
        // Given
        val name = "New Task"
        val description = "A new task"
        val stateName = "Invalid State"

        every { reader.readStringOrNull() } returns name andThen description andThen stateName
        coEvery { manageStateUseCase.getEntityStateIdByName(stateName) } returns UUID.randomUUID()

        // When
        taskManagerUi.addTask("")

        // Then
        verify(exactly = 1) { printer.showMessageLine(any()) }
        coVerify(exactly = 1) { manageStateUseCase.getEntityStateIdByName(stateName) }
        coVerify(exactly = 0) { manageTasksUseCase.addTask(any(), any()) }
    }

    @Test
    fun `createTask() should show error when description is empty`() = runTest {
        // Given
        val name = "New Task"
        every { reader.readStringOrNull() } returns name andThen "" // Empty description

        // When
        taskManagerUi.addTask("")

        // Then
        verify(exactly = 1) { printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT) }
        coVerify(exactly = 0) { manageStateUseCase.getEntityStateIdByName(any()) }
        coVerify(exactly = 0) { manageTasksUseCase.addTask(any(), any()) }
    }

    //endregion

    @Test
    fun `launchUi should call showAllTasks and then exit when options 0 and 7 are selected`() = runTest {
        // Given
        every { reader.readIntOrNull() } returnsMany listOf(0, 7)
        coEvery { manageTasksUseCase.getAllTasks() } returns emptyList()

        // When
        taskManagerUi.launchUi()

        // Then
    }

    @Test
    fun `launchUi should call getTaskByName when option 1 is selected then exit`() {
        // Arrange menu selection: 1 = SHOW_TASK_BY_ID, 7 = EXIT
        every { reader.readIntOrNull() } returnsMany listOf(1, 7)
        every { reader.readStringOrNull() } returns "MyTask"
        coEvery { manageTasksUseCase.getTaskByName("MyTask") } returns mockk(relaxed = true)

        // Act
        taskManagerUi.launchUi()

        // Assert
        coVerify { manageTasksUseCase.getTaskByName("MyTask") }
        verify { printer.printTask(any()) }
    }

    @Test
    fun `launchUi should handle invalid input and loop until valid input`() {
        every { reader.readIntOrNull() } returnsMany listOf(99, 7)

        taskManagerUi.launchUi()

    }
}

