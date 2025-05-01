package ui.features.task

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.input_output.input.InputReader
import org.example.input_output.output.OutputPrinter
import org.example.logic.model.exceptions.ExceptionMessage
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.utils.UiUtils
import org.example.utils.DateHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test



class TaskManagerUiTest {

    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var uiUtils: UiUtils
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var taskManagerUi: TaskManagerUi
    private val dateHandler = DateHandler()

    @BeforeEach
    fun setUp() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        uiUtils = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        taskManagerUi = TaskManagerUi(
            reader,
            printer,
            uiUtils,
            manageTasksUseCase
        )
    }

    @Test
    fun `showAllTasks() should print all tasks when use case succeeds`() {
        // Given
        val sampleTasks = listOf(
            Task(
                name = "Task 1",
                description = "First task",
                stateId = "1",
                createdDate = dateHandler.getCurrentDateTime(),
                updatedDate = dateHandler.getCurrentDateTime()
            ),
            Task(
                name = "Task 2",
                description = "Second task",
                stateId = "2",
                createdDate = dateHandler.getCurrentDateTime(),
                updatedDate = dateHandler.getCurrentDateTime()
            )
        )
        every { manageTasksUseCase.getAllTasks() } returns Result.success(sampleTasks)

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify { printer.showMessage("$sampleTasks") }
    }

    @Test
    fun `showAllTasks() should handle failure gracefully`() {
        // Given
        every { manageTasksUseCase.getAllTasks() } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }
    }

    @Test
    fun `showAllTasks() should handle empty list gracefully`() {
        // Given
        every { manageTasksUseCase.getAllTasks() } returns Result.success(emptyList())

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify { printer.showMessage("${ExceptionMessage.NO_TASKS_FOUNDED}") }
    }

    @Test
    fun `getTaskById() should print task when use case succeeds`() {
        // Given
        val taskId = "1"
        val task = Task(
            id = taskId,
            name = "Task1",
            description = "description of task1",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(task)

        // When
        taskManagerUi.getTaskById()

        // Then
        verify { printer.showMessage("$task") }
    }

    @Test
    fun `getTaskById() should handle failure gracefully`() {
        // Given
        val taskId = "1"
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.getTaskById()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }
    }

    @Test
    fun `createTask() should print success message when use case succeeds`() {
        // Given
        val newTask = Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.success(true)

        // When
        taskManagerUi.createTask()

        // Then
        verify { printer.showMessage("${ExceptionMessage.TASK_CREATED_SUCCESSFULLY}") }
    }

    @Test
    fun `createTask() should handle failure gracefully`() {
        // Given
        val newTask = Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksCreated())

        // When
        taskManagerUi.createTask()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksCreated().message}") }
    }

    @Test
    fun `editTask() should print success message when use case succeeds`() {
        // Given
        val updatedTask = Task(
            name = "New Task",
            description = "Second task",
            stateId = "3",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.editTask(updatedTask) } returns Result.success(true)

        // When
        taskManagerUi.editTask()

        // Then
        verify { printer.showMessage("${ExceptionMessage.TASK_EDITED_SUCCESSFULLY}") }
    }

    @Test
    fun `editTask() should handle failure gracefully`() {
        // Given
        val updatedTask = Task(
            name = "New Task",
            description = "Second task",
            stateId = "3",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.editTask(updatedTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.editTask()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }
    }

    @Test
    fun `deleteTask() should print success message when use case succeeds`() {
        // Given
        val taskId = "1"
        every { manageTasksUseCase.deleteTask(taskId) } returns Result.success(true)

        // When
        taskManagerUi.deleteTask()

        // Then
        verify { printer.showMessage("${ExceptionMessage.TASK_DELETED_SUCCESSFULLY}") }
    }

    @Test
    fun `deleteTask() should handle failure gracefully`() {
        // Given
        val taskId = "1"
        every { manageTasksUseCase.deleteTask(taskId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksDeleted())

        // When
        taskManagerUi.deleteTask()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksDeleted().message}") }
    }

    @Test
    fun `showAllMateTaskAssignment() should print assignments when use case succeeds`() {
        // Given
        val tasks = listOf(MateTaskAssignment("Mate 1", "2"))
        val userName = "Mate 1"
        every { manageTasksUseCase.getAllMateTaskAssignment(userName) } returns Result.success(tasks)

        // When
        taskManagerUi.showAllMateTaskAssignment()

        // Then
        verify { printer.showMessage("$tasks") }
    }

    @Test
    fun `showAllMateTaskAssignment() should handle failure gracefully`() {
        // Given
        val userName = "Mate 1"
        every { manageTasksUseCase.getAllMateTaskAssignment(userName) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.showAllMateTaskAssignment()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }
    }

    @Test
    fun `showAllTasksInProject() should print tasks when use case succeeds`() {
        // Given
        val tasks = listOf(TaskInProject("1", "11", "2"))
        val projectId = "11"
        every { manageTasksUseCase.getAllTasksByProjectId(projectId) } returns Result.success(tasks)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage("$tasks") }
    }

    @Test
    fun `showAllTasksInProject() should handle failure gracefully`() {
        // Given
        val projectId = "11"
        every { manageTasksUseCase.getAllTasksByProjectId(projectId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }
    }

    @Test
    fun `showStateOfProject() should print states when use case succeeds`() {
        // Given
        val projectId = "11"
        val states = listOf(
            "task1" to "state1",
            "task2" to "state2"
        )
        every { manageTasksUseCase.getAllStatesByProjectId(projectId) } returns Result.success(states)

        // When
        taskManagerUi.showStateOfProject()

        // Then
        verify {
            printer.showMessage("1. Task ID: task1, State ID: state1")
            printer.showMessage("2. Task ID: task2, State ID: state2")
        }
    }

    @Test
    fun `showStateOfProject() should handle failure gracefully`() {
        // Given
        val projectId = "11"
        every { manageTasksUseCase.getAllStatesByProjectId(projectId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.showStateOfProject()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }
    }

    @Test
    fun `showStateOfProject() should handle empty state data gracefully`() {
        // Given
        val projectId = "11"
        every { manageTasksUseCase.getAllStatesByProjectId(projectId) } returns Result.success(emptyList())

        // When
        taskManagerUi.showStateOfProject()

        // Then
        verify { printer.showMessage("${ExceptionMessage.NO_STATE_FOUND}") }
    }
}