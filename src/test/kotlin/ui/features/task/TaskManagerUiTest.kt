package ui.features.task


import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.example.logic.entities.Task
import org.example.logic.entities.UserRole
import modle.buildUser
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.utils.UiMessages
import org.example.ui.utils.UiUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildTask
import java.util.*

class TaskManagerUiTest {

    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var uiUtils: UiUtils
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var manageStateUseCase: ManageStatesUseCase
    private lateinit var getProjectUseCase: GetProjectsUseCase
    private lateinit var manageTasksInProjectUseCase: ManageTasksInProjectUseCase
    private lateinit var taskManagerUi: TaskManagerUi

    @BeforeEach
    fun setUp() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        uiUtils = mockk(relaxed = true)
        getProjectUseCase = mockk(relaxed = true)
        manageStateUseCase = mockk(relaxed = true)
        manageTasksInProjectUseCase = mockk(relaxed = true)

        taskManagerUi = TaskManagerUi(
            reader,
            printer,
            uiUtils,
            manageTasksUseCase,
            manageStateUseCase,
            getProjectUseCase,
            manageTasksInProjectUseCase
        )
    }

    //region showAllTasks
    @Test
    fun `showAllTasks() should print all tasks when use case succeeds`() {
        // Given
        val sampleTasks = listOf(
            buildTask(name = "Task 1", description = "First task", stateId = UUID.randomUUID()),
            buildTask(name = "Task 2", description = "Second task", stateId = UUID.randomUUID())
        )
        every { manageTasksUseCase.getAllTasks() } returns Result.success(sampleTasks)

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify(exactly = 1) { printer.printTaskList(sampleTasks) }
        verify(exactly = 0) { printer.showMessage(UiMessages.NO_TASK_FOUND) }
    }

    @Test
    fun `showAllTasks() should show message when no tasks exist`() {
        // Given
        every { manageTasksUseCase.getAllTasks() } returns Result.success(emptyList())

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.NO_TASK_FOUND) }
    }

    @Test
    fun `showAllTasks() should handle failure gracefully`() {
        // Given
        val error = NoTasksFoundException()
        every { manageTasksUseCase.getAllTasks() } returns Result.failure(error)

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify(exactly = 1) { printer.showMessage("Error: ${error}") }
    }
//endregion


    //region getTaskById
    @Test
    fun `getTaskByName() should print task when found`() {
        // Given
        val taskName = "Test Task"
        val task = buildTask(name = taskName, description = "A sample task")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(task)

        // When
        taskManagerUi.getTaskByName()

        // Then
        verifySequence {
            printer.showMessage(UiMessages.TASK_NAME_PROMPT)
            printer.printTask(task)
        }
    }

    @Test
    fun `getTaskByName() should show error when task not found`() {
        // Given
        val taskName = "invalid"

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.failure(NoTasksFoundException())

        // When
        taskManagerUi.getTaskByName()

        // Then
        verify(exactly = 1) {
            printer.showMessage("Error: ${NoTasksFoundException()}")
        }
    }

    @Test
    fun `getTaskByName() should show error when task name is empty`() {
        // Given
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.getTaskByName()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT) }
        verify(exactly = 0) { manageTasksUseCase.getTaskByName(any()) }
    }

    @Test
    fun `getTaskByName() should show error when task name is blank`() {
        // Given
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.getTaskByName()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT) }
        verify(exactly = 0) { manageTasksUseCase.getTaskByName(any()) }
    }
    //endregion

    //region createTask
    @Test
    fun `createTask() should create task when valid input is provided`() {
        // Given
        val name = "New Task"
        val description = "A new task"
        val stateName = "TODO"
        val stateId = UUID.randomUUID()

        every { reader.readStringOrNull() } returns name andThen description andThen stateName
        every { manageStateUseCase.getProjectStateIdByName(stateName) } returns stateId
        every { manageTasksUseCase.addTask(any()) } returns Result.success(true)

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { manageStateUseCase.getProjectStateIdByName(stateName) }
        verify(exactly = 1) { manageTasksUseCase.addTask(any()) }
        verify(exactly = 1) { printer.printTask(any()) }
    }

    @Test
    fun `createTask() should show error when name is empty`() {
        // Given
        every { reader.readStringOrNull() } returns "" // Empty name

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_INPUT) }
        verify(exactly = 0) { manageStateUseCase.getProjectStateIdByName(any()) }
        verify(exactly = 0) { manageTasksUseCase.addTask(any()) }
    }

    @Test
    fun `createTask() should show error when state name is empty`() {
        // Given
        val name = "New Task"
        val description = "A new task"
        every { reader.readStringOrNull() } returns name andThen description andThen ""

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_INPUT) }
        verify(exactly = 0) { manageStateUseCase.getProjectStateIdByName(any()) }
        verify(exactly = 0) { manageTasksUseCase.addTask(any()) }
    }

    @Test
    fun `createTask() should show error when state name is invalid`() {
        // Given
        val name = "New Task"
        val description = "A new task"
        val stateName = "Invalid State"

        every { reader.readStringOrNull() } returns name andThen description andThen stateName
        every { manageStateUseCase.getProjectStateIdByName(stateName) } returns null

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.INVALID_TASK_STATE_INPUT) }
        verify(exactly = 1) { manageStateUseCase.getProjectStateIdByName(stateName) }
        verify(exactly = 0) { manageTasksUseCase.addTask(any()) }
    }

    @Test
    fun `createTask() should show error when description is empty`() {
        // Given
        val name = "New Task"
        every { reader.readStringOrNull() } returns name andThen "" // Empty description

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_INPUT) }
        verify(exactly = 0) { manageStateUseCase.getProjectStateIdByName(any()) }
        verify(exactly = 0) { manageTasksUseCase.addTask(any()) }
    }

    //endregion

    //region editTask
    @Test
    fun `editTask() should update task successfully when looked up by name`() {
        // Given
        val taskName = "Old Name"
        val newName = "Updated"
        val newDescription = "Updated Desc"
        val newStateName = "Closed"
        val newStateId = UUID.randomUUID()

        val existingTask = buildTask(
            name = taskName,
            description = "Old Description",
            stateId = UUID.randomUUID()
        )

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen newStateName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { manageStateUseCase.getProjectStateIdByName(newStateName) } returns newStateId
        every { manageTasksUseCase.editTask(any()) } returns Result.success(true)

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { manageTasksUseCase.editTask(any()) }
        verify(exactly = 1) { printer.printTask(any()) }
    }

    @Test
    fun `editTask() should show error when task not found by name`() {
        // Given
        val taskName = "invalid"
        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.failure(NoTasksFoundException())

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.NO_TASK_FOUND) }
    }

    @Test
    fun `editTask() should show error when state name is empty`() {
        // Given
        val taskName = "Updated"
        val newName = "Updated"
        val newDescription = "Updated Desc"
        val existingTask = buildTask(name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT) }
    }

    @Test
    fun `editTask() should show error when task ID is empty`() {
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT) }
        verify(exactly = 0) { reader.readStringOrNull() }
    }

    @Test
    fun `editTask() should show error when task ID is blank`() {
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT) }
    }

    @Test
    fun `editTask() should show error when new name is empty`() {
        // Given
        val taskName = "Old"
        val newState = "Updated"
        val newDescription = "Updated Desc"
        val existingTask = buildTask(name = taskName, description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns "" andThen newDescription andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT) }
    }

    @Test
    fun `editTask() should show error when new name is null`() {
        // Given
        val taskName = "Old"
        val newState = "Updated"
        val newDescription = "Updated Desc"
        val existingTask = buildTask( name = taskName, description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns null andThen newDescription andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT) }
    }

    @Test
    fun `editTask() should show error when new description is empty`() {
        // Given
        val taskName = "Old"
        val newName = "Updated"
        val newState = "Updated"
        val existingTask = buildTask(name = taskName, description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen "" andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_DESCRIPTION_INPUT) }
    }

    @Test
    fun `editTask() should show error when new description is null`() {
        // Given
        val taskName = "Old"
        val newName = "Updated"
        val newState = "Updated"
        val existingTask = buildTask(name = taskName, description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen null andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_DESCRIPTION_INPUT) }
    }

    @Test
    fun `editTask() should show error when new state name is empty`() {
        // Given
        val taskName = "Old"
        val newName = "Updated"
        val newDescription = "updated"
        val existingTask = buildTask(name = taskName, description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen ""

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT) }
    }

    @Test
    fun `editTask() should show error when new state name is null`() {
        // Given
        val taskName = "Old"
        val newName = "Updated"
        val newDescription = "updated"
        val existingTask = buildTask(name = taskName, description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT) }
    }

    @Test
    fun `editTask() should show EMPTY_TASK_INPUT when any input is empty`() {
        // Given
        val taskName = "Old"
        val existingTask = buildTask(name = taskName, description =  "Old Desc", stateId = UUID.randomUUID())

        every { uiUtils.readNonBlankInputOrNull(reader) } returnsMany listOf(
            taskName, "New Name", "New Desc", null
        )
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)

        // When
        taskManagerUi.editTask()

        // Then
        verify { printer.showMessage(UiMessages.EMPTY_TASK_INPUT) }
    }

    @Test
    fun `editTask() should show error when invalid state name is provided`() {
        // Given
        val taskName = "Old Task"
        val newName = "New Name"
        val newDescription = "New Description"
        val newStateName = "Invalid State"
        val existingTask = buildTask(name = taskName)

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen newStateName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(existingTask)
        every { manageStateUseCase.getProjectStateIdByName(newStateName) } returns null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.INVALID_STATE_NAME) }
    }

    //endregion

    //region deleteTask
    @Test
    fun `deleteTask should delete task successfully`() {
        // Given
        val taskName = "Test Task"
        val dummyTask = buildTask(name = taskName)

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.success(dummyTask)
        every { manageTasksUseCase.deleteTaskByName(taskName) } returns Result.success(true)

        // When
        taskManagerUi.deleteTask()

        // Then
        verify(exactly = 1) { manageTasksUseCase.getTaskByName(taskName) }
        verify(exactly = 1) { manageTasksUseCase.deleteTaskByName(taskName) }
        verify(exactly = 1) { printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY) }
    }


    @Test
    fun `deleteTask should show error when task name is empty`() {
        // Given
        val taskName = ""
        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName

        // When
        taskManagerUi.deleteTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT) }
    }

    @Test
    fun `deleteTask should show error when task not found`() {
        // Given
        val taskName = "Non-existing Task"
        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName
        every { manageTasksUseCase.getTaskByName(taskName) } returns Result.failure(NoTasksFoundException())

        // When
        taskManagerUi.deleteTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.NO_TASK_FOUND) }
    }
    //endregion

    //region showAllMateTaskAssignment
    @Test
    fun `showAllTasksInProject() should display tasks when valid tasks are found`() {
        // Given
        val projectName = "Project X"
        val tasks = listOf(
            buildTask(name = "Task 1"),
            buildTask(name = "Task 2")
        )

        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectName
        every { manageTasksInProjectUseCase.getTasksInProjectByName(projectName) } returns Result.success(tasks)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify(exactly = 1) { printer.printTaskList(tasks) }
    }

    @Test
    fun `showAllTasksInProject() should show message when no tasks are found in project`() {
        // Given
        val projectName = "Project X"
        val emptyTaskList = emptyList<Task>()

        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectName
        every { manageTasksInProjectUseCase.getTasksInProjectByName(projectName) } returns Result.success(emptyTaskList)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT) }
    }
    //endregion

    //region showAllTasksInProject
    @Test
    fun `showAllTasksInProject() should display tasks when tasks are found in project`() {
        // Given
        val projectId = "proj-1"
        val tasks = listOf(buildTask(name = "Task 1"), buildTask(name = "Task 2"))
        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectId
        every { manageTasksInProjectUseCase.getTasksInProjectByName(projectId) } returns Result.success(tasks)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_NAME_PROMPT) }
        verify { printer.printTaskList(tasks) }
        verify(exactly = 0) { printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT) }
    }


    @Test
    fun `showAllTasksInProject() should show no tasks message when project has no tasks`() {
        val projectName = "proj-2"
        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectName
        every { manageTasksInProjectUseCase.getTasksInProjectByName(projectName) } returns Result.success(emptyList())

        taskManagerUi.showAllTasksInProject()

        verify { printer.showMessage(UiMessages.PROJECT_NAME_PROMPT) }
        verify { printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT) }
        verify(exactly = 0) { printer.printTaskList(any()) }
    }

    @Test
    fun `showAllTasksInProject() should show empty project ID message when input is blank`() {
        // Given
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_NAME_PROMPT) }
        verify { printer.showMessage(UiMessages.EMPTY_PROJECT_ID_INPUT) }
        verify(exactly = 0) { manageTasksInProjectUseCase.getTasksInProjectByName(any()) }
    }

    @Test
    fun `showAllTasksInProject() should handle failure from use case gracefully`() {
        // Given
        val projectId = "proj-3"
        val exception = NoTaskAssignmentFoundException()
        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectId
        every { manageTasksInProjectUseCase.getTasksInProjectByName(projectId) } returns Result.failure(exception)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_NAME_PROMPT) }
        verify { printer.showMessage("Error: ${exception}") }
    }
    //endregion

    @Test
    fun `launchUi should call showAllTasks and then exit when options 0 and 7 are selected`() {
        // Given
        every { reader.readIntOrNull() } returnsMany listOf(0, 7)
        every { manageTasksUseCase.getAllTasks() } returns Result.success(emptyList())

        // When
        taskManagerUi.launchUi(buildUser("123", "test", UserRole.MATE))

        // Then
        verify(exactly = 1) { taskManagerUi.showAllTasks() }
        verify(exactly = 1) { uiUtils.exit() }
    }

    @Test
    fun `launchUi should call getTaskByName when option 1 is selected then exit`() {
        // Arrange menu selection: 1 = SHOW_TASK_BY_ID, 7 = EXIT
        every { reader.readIntOrNull() } returnsMany listOf(1, 7)
        every { reader.readStringOrNull() } returns "MyTask"
        every { manageTasksUseCase.getTaskByName("MyTask") } returns Result.success(mockk(relaxed = true))

        // Act
        taskManagerUi.launchUi(null)

        // Assert
        verify { manageTasksUseCase.getTaskByName("MyTask") }
        verify { printer.printTask(any()) }
        verify { uiUtils.exit() }
    }

    @Test
    fun `launchUi should handle invalid input and loop until valid input`() {
        every { reader.readIntOrNull() } returnsMany listOf(99, 7)

        taskManagerUi.launchUi(null)

        verify { uiUtils.invalidChoice() }
        verify { uiUtils.exit() }
    }
}

