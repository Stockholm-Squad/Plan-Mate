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
import org.example.utils.DateHandler
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class ManageTasksUiTest {

    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var taskManagerUi: TaskManagerUi
    private val dateHandler = DateHandler()
    private lateinit var printer: OutputPrinter
    private lateinit var reader: InputReader

    @BeforeEach
    fun setUp() {
        manageTasksUseCase = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        taskManagerUi = TaskManagerUi(manageTasksUseCase, reader, printer)
    }

    private val sampleTasks = listOf(
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

    // showAllTasks
    @Test
    fun `showAllTasks() should return all tasks when use case succeeds`() {
        //Given
        every { manageTasksUseCase.getAllTasks() } returns Result.success(sampleTasks)
        //When
        taskManagerUi.showAllTasks()
        //Then
        verify { printer.showMessage("$sampleTasks") }
    }

    @Test
    fun `showAllTasks() should  failure when use case fails`() {
        //Given
        every { manageTasksUseCase.getAllTasks() } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
        //When
        taskManagerUi.showAllTasks()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    @Test
    fun `showAllTasks() should handle empty list gracefully`() {
        //Given
        every { manageTasksUseCase.getAllTasks() } returns Result.success(emptyList())
        //When
        taskManagerUi.showAllTasks()
        //Then
        verify { printer.showMessage("${ExceptionMessage.NO_TASKS_FOUNDED}") }
    }

    // getTaskById
    @Test
    fun `getTaskById() should return all tasks when use case succeeds`() {
        //Given
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
        //When
        taskManagerUi.getTaskById()
        //Then
        verify { printer.showMessage("$task") }
    }

    @Test
    fun `getTaskById() should  failure when use case fails`() {
        //Given
        val taskId = "1"
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
        //When
        taskManagerUi.getTaskById()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    @Test
    fun `getTaskById() should  failure when taskId is null`() {
        //Given
        val taskId = null
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
        //When
        taskManagerUi.getTaskById()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    //createTask
    @Test
    fun `createTask() should return true when use case succeeds`() {
        //Given
        val newTask = Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.success(true)

        //When
        taskManagerUi.createTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.TASK_CREATED_SUCCESSFULLY}") }

    }

    @Test
    fun `createTask() should  failure when use case fails`() {
        //Given
        val newTask = Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksCreated())

        //When
        taskManagerUi.createTask()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksCreated().message}") }

    }

    @Test
    fun `createTask() should return false when name is null`() {
        //Given
        val newTask = Task(
            name = null,
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.success(false)

        //When
        taskManagerUi.createTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.FAILED_TO_CREATE_TASK}") }

    }

    @Test
    fun `createTask() should return false when description is null`() {
        //Given
        val newTask = Task(
            name = "Task1",
            description = null,
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.success(false)

        //When
        taskManagerUi.createTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.FAILED_TO_CREATE_TASK}") }

    }

    @Test
    fun `createTask() should return false when stateId is null`() {
        //Given
        val newTask = Task(
            name = "Task1",
            description = "Second task",
            stateId = null,
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.success(false)

        //When
        taskManagerUi.createTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.FAILED_TO_CREATE_TASK}") }

    }

    @Test
    fun `createTask() should fail when name is empty`() {
        val emptyTask = Task(
            name = "",
            description = "Task with no name",
            stateId = "1",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(emptyTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksCreated())

        taskManagerUi.createTask()
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksCreated().message}") }
    }


    //editTask
    @Test
    fun `editTask() should return true when use case succeeds`() {
        //Given
        val updatedTask = Task(
            name = "New Task",
            description = "Second task",
            stateId = "3",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.editTask(updatedTask) } returns Result.success(true)

        //When
        taskManagerUi.editTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.TASK_EDITED_SUCCESSFULLY}") }

    }

    @Test
    fun `editTask() should failure when use case fails`() {
        //Given
        val updatedTask = Task(
            name = "New Task",
            description = "Second task",
            stateId = "3",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.editTask(updatedTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        //When
        taskManagerUi.editTask()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    @Test
    fun `editTask() should return false with invalid taskId`() {
        val invalidTask = Task(
            id = "invalid",
            name = "Invalid Task",
            description = "Invalid task description",
            stateId = "999",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.editTask(invalidTask) } returns Result.success(false)

        taskManagerUi.editTask()
        verify { printer.showMessage("${ExceptionMessage.FAILED_TO_EDIT_TASK}") }
    }

    //deleteTask
    @Test
    fun `deleteTask() should return true when use case succeeds`() {
        //Given
        val taskId = "1"
        every { manageTasksUseCase.deleteTask(taskId) } returns Result.success(true)

        //When
        taskManagerUi.deleteTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.TASK_DELETED_SUCCESSFULLY}") }

    }

    @Test
    fun `deleteTask() should failure when use case fails`() {
        //Given
        val taskId = "1"
        every { manageTasksUseCase.deleteTask(taskId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksDeleted())

        //When
        taskManagerUi.deleteTask()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksDeleted().message}") }

    }

    @Test
    fun `deleteTask() should return false when taskId is null`() {
        //Given
        val taskId = null
        every { manageTasksUseCase.deleteTask(taskId) } returns Result.success(false)
        //When
        taskManagerUi.deleteTask()
        //Then
        verify { printer.showMessage("${ExceptionMessage.TASK_NOT_FOUND}") }

    }

    //showAllMateTaskAssignment
    @Test
    fun `showAllMateTaskAssignment() should return all tasks when use case succeeds`() {
        //Given
        val tasks = listOf(MateTaskAssignment("Mate 1", "2"))
        val userName = "Mate 1"
        every { manageTasksUseCase.getAllMateTaskAssignment(userName) } returns Result.success(tasks)
        //When
        taskManagerUi.showAllTasks()
        //Then
        verify { printer.showMessage("$tasks") }
    }

    @Test
    fun `showAllMateTaskAssignment() should  failure when use case fails`() {
        //Given
        val userName = "Mate 1"
        every { manageTasksUseCase.getAllMateTaskAssignment(userName) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
        //When
        taskManagerUi.showAllMateTaskAssignment()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    //getAllTasksByProjectId
    @Test
    fun `getAllTasksByProjectId() should return all tasks when use case succeeds`() {
        //Given
        val tasks = listOf(TaskInProject("1", "11", "2"))
        val projectId = "11"
        every { manageTasksUseCase.getAllTasksByProjectId(projectId) } returns Result.success(tasks)
        //When
        taskManagerUi.showAllTasksInProject()
        //Then
        verify { printer.showMessage("$tasks") }
    }

    @Test
    fun `showAllTasksInProject() should  failure when use case fails`() {
        //Given
        val projectId = "11"
        every { manageTasksUseCase.getAllTasksByProjectId(projectId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
        //When
        taskManagerUi.showAllTasksInProject()
        //Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    //showStateOfProject
    @Test
    fun `showStateOfProject() should print all states when use case succeeds`() {
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
        verify { printer.showMessage("1. Task ID: task1, State ID: state1") }
        verify { printer.showMessage("2. Task ID: task2, State ID: state2") }
    }

    @Test
    fun `showStateOfProject() should show failure message when use case fails`() {
        // Given
        val projectId = "11"
        val exception = PlanMateExceptions.LogicException.NoTasksFound()

        every { manageTasksUseCase.getAllStatesByProjectId(projectId) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        taskManagerUi.showStateOfProject()

        // Then
        verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}") }

    }

    @Test
    fun `showStateOfProject() should handle empty state data gracefully`() {
        val projectId = "11"
        val emptyStates = emptyList<Pair<String, String>>()

        every { manageTasksUseCase.getAllStatesByProjectId(projectId) } returns Result.success(emptyStates)

        taskManagerUi.showStateOfProject()
        verify { printer.showMessage("${ExceptionMessage.NO_TASKS_FOUNDED}") }
    }


}