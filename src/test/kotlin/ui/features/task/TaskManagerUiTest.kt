package ui.features.task


import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import data.models.MateTaskAssignment
import org.example.logic.model.exceptions.NoTasksFound
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.example.logic.model.exceptions.NoTaskAssignmentFound
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.GetTasksAssignedToUserUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.utils.UiMessages
import org.example.ui.utils.UiUtils
import org.example.utils.TaskOptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildMateTaskAssignment
import utils.buildTask

class TaskManagerUiTest {

    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var uiUtils: UiUtils
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var manageStateUseCase: ManageStatesUseCase
    private lateinit var manageTasksInProjectUseCase: ManageTasksInProjectUseCase
    private lateinit var getTasksAssignedToUserUseCase: GetTasksAssignedToUserUseCase
    private lateinit var taskManagerUi: TaskManagerUi

    @BeforeEach
    fun setUp() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        uiUtils = mockk(relaxed = true)

        manageTasksUseCase = mockk(relaxed = true)
        manageStateUseCase = mockk(relaxed = true)
        manageTasksInProjectUseCase = mockk(relaxed = true)
        getTasksAssignedToUserUseCase = mockk(relaxed = true)

        taskManagerUi = TaskManagerUi(
            reader,
            printer,
            uiUtils,
            manageTasksUseCase,
            manageStateUseCase,
            manageTasksInProjectUseCase,
            getTasksAssignedToUserUseCase
        )
    }

    //region showAllTasks
    @Test
    fun `showAllTasks() should print all tasks when use case succeeds`() {
        // Given
        val sampleTasks = listOf(
            buildTask(name = "Task 1", description = "First task", stateId = "1"),
            buildTask(name = "Task 2", description = "Second task", stateId = "2")
        )
        every { manageTasksUseCase.getAllTasks() } returns Result.success(sampleTasks)

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify(exactly = 1) { printer.printTaskList(sampleTasks) }
        verify(exactly = 0) { printer.showMessage(UiMessages.NO_TASK_FOUNDED.message) }
    }

    @Test
    fun `showAllTasks() should show message when no tasks exist`() {
        // Given
        every { manageTasksUseCase.getAllTasks() } returns Result.success(emptyList())

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.NO_TASK_FOUNDED.message) }
    }

    @Test
    fun `showAllTasks() should handle failure gracefully`() {
        // Given
        val error = NoTasksFound()
        every { manageTasksUseCase.getAllTasks() } returns Result.failure(error)

        // When
        taskManagerUi.showAllTasks()

        // Then
        verify(exactly = 1) { printer.showMessage("Error: ${error.message}") }
    }
//endregion


    //region getTaskById
    @Test
    fun `getTaskById() should print task when found`() {
        // Given
        val taskId = "valid-id"
        val task = buildTask(id = taskId, name = "Test Task", description = "A sample task")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(task)

        // When
        taskManagerUi.getTaskById()

        // Then
        verifySequence {
            printer.showMessage(UiMessages.TASK_ID_PROMPT.message)
            printer.printTask(task)
        }
    }

    @Test
    fun `getTaskById() should show error when task not found`() {
        // Given
        val taskId = "invalid"

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.failure(NoTasksFound())

        // When
        taskManagerUi.getTaskById()

        // Then
        verify(exactly = 1) {
            printer.showMessage("Error: ${NoTasksFound().message}")
        }
    }

    @Test
    fun `getTaskById() should show error when task ID is empty`() {
        // Given
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.getTaskById()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT.message) }
        verify(exactly = 0) { manageTasksUseCase.getTaskById(any()) }
    }

    @Test
    fun `getTaskById() should show error when task ID is blank`() {
        // Given
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.getTaskById()

        // Then
        verify() { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT.message) }
    }

    //endregion

    //region createTask
    @Test
    fun `createTask() should create task when valid input is provided`() {
        // Given
        val name = "New Task"
        val description = "A new task"
        val stateName = "TODO"
        val stateId = "1"

        every { reader.readStringOrNull() } returns name andThen description andThen stateName
        every { manageStateUseCase.getProjectStateIdByName(stateName) } returns stateId
        every { manageTasksUseCase.createTask(any()) } returns Result.success(true)

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { manageStateUseCase.getProjectStateIdByName(stateName) }
        verify(exactly = 1) { manageTasksUseCase.createTask(any()) }
        verify(exactly = 1) { printer.printTask(any()) }
    }

    @Test
    fun `createTask() should show error when name is empty`() {
        // Given
        every { reader.readStringOrNull() } returns "" // Empty name

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_INPUT.message) }
        verify(exactly = 0) { manageStateUseCase.getProjectStateIdByName(any()) }
        verify(exactly = 0) { manageTasksUseCase.createTask(any()) }
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
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_INPUT.message) }
        verify(exactly = 0) { manageStateUseCase.getProjectStateIdByName(any()) }
        verify(exactly = 0) { manageTasksUseCase.createTask(any()) }
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
        verify(exactly = 1) { printer.showMessage(UiMessages.INVALID_TASK_STATE_INPUT.message) }
        verify(exactly = 1) { manageStateUseCase.getProjectStateIdByName(stateName) }
        verify(exactly = 0) { manageTasksUseCase.createTask(any()) }
    }

    @Test
    fun `createTask() should show error when description is empty`() {
        // Given
        val name = "New Task"
        every { reader.readStringOrNull() } returns name andThen "" // Empty description

        // When
        taskManagerUi.createTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_INPUT.message) }
        verify(exactly = 0) { manageStateUseCase.getProjectStateIdByName(any()) }
        verify(exactly = 0) { manageTasksUseCase.createTask(any()) }
    }

    //endregion

    //region editTask
    @Test
    fun `editTask() should update task successfully`() {
        // Given
        val taskId = "123"
        val newName = "Updated"
        val newDescription = "Updated Desc"
        val newStateName = "Closed"
        val newStateId = "2"

        val existingTask = buildTask(
            id = taskId,
            name = "Old Name",
            description = "Old Description",
            stateId = "1"
        )

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen newStateName
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { manageStateUseCase.getProjectStateIdByName(newStateName) } returns newStateId
        every { manageTasksUseCase.editTask(any()) } returns Result.success(true)

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { manageTasksUseCase.editTask(any()) }
        verify(exactly = 1) { printer.printTask(any()) }
    }

    @Test
    fun `editTask() should show error when task not found`() {
        val taskId = "invalid"
        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.failure(NoTasksFound())

        taskManagerUi.editTask()

        verify(exactly = 1) { printer.showMessage(UiMessages.NO_TASK_FOUNDED.message) }
    }

    @Test
    fun `editTask() should show error when state name is empty`() {
        // Given
        val taskId = "123"
        val newName = "Updated"
        val newDescription = "Updated Desc"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when task ID is empty`() {
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT.message) }
        verify(exactly = 0) { reader.readStringOrNull() }
    }

    @Test
    fun `editTask() should show error when task ID is blank`() {
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when new name is empty`() {
        // Given
        val taskId = "123"
        val newState = "Updated"
        val newDescription = "Updated Desc"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns "" andThen newDescription andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when new name is null`() {
        // Given
        val taskId = "123"
        val newState = "Updated"
        val newDescription = "Updated Desc"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns null andThen newDescription andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_NAME_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when new description is empty`() {
        // Given
        val taskId = "123"
        val newName = "Updated"
        val newState = "Updated"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen "" andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_DESCRIPTION_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when new description is null`() {
        // Given
        val taskId = "123"
        val newName = "Updated"
        val newState = "Updated"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen null andThen newState

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_DESCRIPTION_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when new state name is empty`() {
        // Given
        val taskId = "123"
        val newName = "Updated"
        val newDescription = "updated"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen ""

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when new state name is null`() {
        // Given
        val taskId = "123"
        val newName = "Updated"
        val newDescription = "updated"
        val existingTask = buildTask(id = "taskId", name = "Old", description = "Old Desc")

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_STATE_INPUT.message) }
    }

    @Test
    fun `editTask() should show EMPTY_TASK_INPUT when any input is empty`() {
        // Given
        val taskId = "123"
        val existingTask = buildTask(taskId, "Old", "Old Desc", "1")

        every { uiUtils.readNonBlankInputOrNull(reader) } returnsMany listOf(
            taskId, "New Name", "New Desc", null
        )
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)

        // When
        taskManagerUi.editTask()

        // Then
        verify { printer.showMessage(UiMessages.EMPTY_TASK_INPUT.message) }
    }

    @Test
    fun `editTask() should show error when invalid state name is provided`() {
        // Given
        val taskId = "123"
        val newName = "New Name"
        val newDescription = "New Description"
        val newStateName = "Invalid State"
        val existingTask = buildTask(id = taskId)

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { reader.readStringOrNull() } returns newName andThen newDescription andThen newStateName
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(existingTask)
        every { manageStateUseCase.getProjectStateIdByName(newStateName) } returns null

        // When
        taskManagerUi.editTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.INVALID_STATE_NAME.message) }
    }

    //endregion

    //region deleteTask
    @Test
    fun `deleteTask should delete task successfully`() {
        // Given
        val taskId = "123"
        val dummyTask = buildTask(id = taskId)

        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskId
        every { manageTasksUseCase.getTaskById(taskId) } returns Result.success(dummyTask)
        every { manageTasksUseCase.deleteTask(taskId) } returns Result.success(true)

        // When
        taskManagerUi.deleteTask()

        // Then
        verify(exactly = 1) { manageTasksUseCase.getTaskById(taskId) }
        verify(exactly = 1) { manageTasksUseCase.deleteTask(taskId) }
        verify(exactly = 1) { printer.showMessage(UiMessages.TASK_DELETE_SUCCESSFULLY.message) }
    }

    @Test
    fun `deleteTask should show error when task ID is empty`() {
        // Given
        val id = ""
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null
        every { manageTasksUseCase.deleteTask(id) } returns Result.success(false)

        // When
        taskManagerUi.deleteTask()

        // Then
        verify(exactly = 1) { printer.showMessage(UiMessages.EMPTY_TASK_ID_INPUT.message) }
    }

    //endregion

    //region showAllMateTaskAssignment
    @Test
    fun `showAllMateTaskAssignment() should show assignments for valid user`() {
        // Given
        val userName = "Alice"
        val assignments = listOf(
            buildMateTaskAssignment(userName = "Alice", taskId = "1"),
            buildMateTaskAssignment(userName = "Alice", taskId = "2")
        )

        every { uiUtils.readNonBlankInputOrNull(reader) } returns userName
        every { getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName) } returns Result.success(assignments)

        // When
        taskManagerUi.showAllMateTaskAssignment()

        // Then
        verify(exactly = 1) { printer.printMateTaskAssignments(assignments) }
    }

    @Test
    fun `showAllMateTaskAssignment() should show message when no assignments exist`() {
        // Given
        val userName = "Asmaa"
        val emptyAssignments = emptyList<MateTaskAssignment>()

        every { uiUtils.readNonBlankInputOrNull(reader) } returns userName
        every { getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName) } returns Result.success(emptyAssignments)

        // When
        taskManagerUi.showAllMateTaskAssignment()

        // Then
        verify(exactly = 1) { printer.printMateTaskAssignments(emptyAssignments) }
    }

    @Test
    fun `showAllMateTaskAssignment() should handle failure gracefully`() {
        // Given
        val userName = "Asmaa"

        every { uiUtils.readNonBlankInputOrNull(reader) } returns userName
        every { getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName) } returns
                Result.failure(NoTaskAssignmentFound())

        // When
        taskManagerUi.showAllMateTaskAssignment()

        // Then
        verify(exactly = 1) { printer.showMessage("Error: ${NoTaskAssignmentFound().message}") }
    }
    //endregion

    //region showAllTasksInProject
    @Test
    fun `showAllTasksInProject() should display tasks when tasks are found in project`() {
        // Given
        val projectId = "proj-1"
        val tasks = listOf(buildTask("1", "Task 1"), buildTask("2", "Task 2"))

        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectId
        every { manageTasksInProjectUseCase.getTasksAssignedToProject(projectId) } returns Result.success(tasks)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_ID_PROMPT.message) }
        verify { printer.printTaskList(tasks) }
        verify(exactly = 0) { printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT.message) }
    }

    @Test
    fun `showAllTasksInProject() should show no tasks message when project has no tasks`() {
        // Given
        val projectId = "proj-2"
        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectId
        every { manageTasksInProjectUseCase.getTasksAssignedToProject(projectId) } returns Result.success(emptyList())

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_ID_PROMPT.message) }
        verify { printer.showMessage(UiMessages.NO_TASKS_FOUND_IN_PROJECT.message) }
        verify(exactly = 0) { printer.printTaskList(any()) }
    }

    @Test
    fun `showAllTasksInProject() should show empty project ID message when input is blank`() {
        // Given
        every { uiUtils.readNonBlankInputOrNull(reader) } returns null

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_ID_PROMPT.message) }
        verify { printer.showMessage(UiMessages.EMPTY_PROJECT_ID_INPUT.message) }
        verify(exactly = 0) { manageTasksInProjectUseCase.getTasksAssignedToProject(any()) }
    }

    @Test
    fun `showAllTasksInProject() should handle failure from use case gracefully`() {
        // Given
        val projectId = "proj-3"
        val exception = NoTaskAssignmentFound()
        every { uiUtils.readNonBlankInputOrNull(reader) } returns projectId
        every { manageTasksInProjectUseCase.getTasksAssignedToProject(projectId) } returns Result.failure(exception)

        // When
        taskManagerUi.showAllTasksInProject()

        // Then
        verify { printer.showMessage(UiMessages.PROJECT_ID_PROMPT.message) }
        verify { printer.showMessage("Error: ${exception.message}") }
    }
    //endregion

    @Test
    fun `launchUi() should print task options menu and exit on valid exit input`() {
        // Given
        every { reader.readIntOrNull() } returns TaskOptions.EXIT.option
        every { uiUtils.getEnteredOption(TaskOptions.EXIT.option) } returns TaskOptions.EXIT

        // When
        taskManagerUi.launchUi()

        // Then
        verify { uiUtils.exit() }


    }
    @Test
    fun `launchUi() should call showAllTasks then exit`() {
        // Given
        every { reader.readIntOrNull() } returnsMany listOf(
            TaskOptions.SHOW_ALL_TASKS.option,
            TaskOptions.EXIT.option
        )
        every { uiUtils.getEnteredOption(TaskOptions.SHOW_ALL_TASKS.option) } returns TaskOptions.SHOW_ALL_TASKS
        every { uiUtils.getEnteredOption(TaskOptions.EXIT.option) } returns TaskOptions.EXIT

        every { manageTasksUseCase.getAllTasks() } returns Result.success(emptyList())

        // when
        taskManagerUi.launchUi()

        // Then
        verify(exactly = 1) { manageTasksUseCase.getAllTasks() }
        verify { uiUtils.exit() }
    }

}
