package ui.features.task

import com.google.common.truth.Truth.assertThat
import data.repo.stateId1
import data.repo.taskId1
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import logic.usecase.audit.projectId
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.EntityType
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.common.utils.UiUtils
import org.example.ui.features.task.TaskInput
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildTask
import java.util.*


class TaskManagerUiTest {

    private lateinit var reader: InputReader
    private lateinit var printer: OutputPrinter
    private lateinit var uiUtils: UiUtils
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var manageStateUseCase: ManageEntityStatesUseCase
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var auditServicesUseCase: AuditServicesUseCase
    private lateinit var loginUseCase: LoginUseCase

    private lateinit var taskManagerUi: TaskManagerUi

    @BeforeEach
    fun setup() {
        reader = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        uiUtils = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        manageStateUseCase = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        auditServicesUseCase = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)

        taskManagerUi = TaskManagerUi(
            reader, printer, uiUtils,
            manageTasksUseCase, manageStateUseCase,
            getProjectsUseCase, auditServicesUseCase, loginUseCase
        )
    }

    @Test
    fun `showAllTasks() should print all tasks when use case succeeds`() = runBlocking {
        // Given
        val tasks = listOf(
            buildTask(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Task1"),
            buildTask(UUID.fromString("223e4567-e89b-12d3-a456-426614174001"), "Task2")
        )
        coEvery { manageTasksUseCase.getAllTasks() } returns tasks

        // When
        taskManagerUi.showAllTasks()

        // Then
        coVerify { printer.printTaskList(tasks) }
    }

    @Test
    fun `showAllTasks() should print unknown error when use case throws exception`() = runBlocking {
        // Given
        coEvery { manageTasksUseCase.getAllTasks() } throws RuntimeException("error happened")

        // When
        taskManagerUi.showAllTasks()

        // Then
        coVerify { printer.showMessageLine("error happened") }
    }

    @Test
    fun `addTask() should add task and print it when inputs are valid`() = runBlocking {
        // Given
        val projectName = "MyProject"
        val taskInput = TaskInput("taskName", "desc", "stateName")

        every { reader.readStringOrNull() } returnsMany listOf(
            projectName,
            taskInput.name,
            taskInput.description,
            taskInput.stateName
        )
        coEvery { manageStateUseCase.getEntityStateIdByName(taskInput.stateName) } returns stateId1
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk(relaxed = true) {
            every { id } returns projectId
            every { title } returns projectName
        }
        coEvery { manageTasksUseCase.addTask(any(), projectId) } returns true
        coEvery { manageTasksUseCase.addTaskToProject(projectId, any()) } returns true
        coEvery { auditServicesUseCase.addAuditForAddEntity(EntityType.TASK, any(), any(), any()) } just Runs

        // When
        taskManagerUi.addTask(null)

        // Then
        coVerifySequence {
            printer.showMessageLine(UiMessages.PROJECT_NAME_PROMPT)
            reader.readStringOrNull()
            getProjectsUseCase.getProjectByName(projectName)
            printer.showMessageLine(UiMessages.TASK_NAME_PROMPT)
            reader.readStringOrNull()
            printer.showMessageLine(UiMessages.TASK_DESCRIPTION_PROMPT)
            reader.readStringOrNull()
            printer.showMessageLine(UiMessages.TASK_STATE_PROMPT)
            reader.readStringOrNull()
            manageStateUseCase.getEntityStateIdByName(taskInput.stateName)
            getProjectsUseCase.getProjectByName(projectName)
            manageTasksUseCase.addTask(any(), projectId)
            manageTasksUseCase.addTaskToProject(projectId, any())
            auditServicesUseCase.addAuditForAddEntity(EntityType.TASK, any(), any(), any())
            printer.printTask(any())
        }
    }

    @Test
    fun `updateTask() should update task and print it when inputs are valid`() = runBlocking {
        // Given
        val projectName = "MyProject"
        val oldTask = buildTask(taskId1, "OldTask")
        val updatedName = "NewTask"
        val updatedDescription = "NewDesc"
        val updatedStateName = "NewState"

        every { reader.readStringOrNull() } returnsMany listOf(
            projectName,
            updatedName,
            updatedDescription,
            updatedStateName
        )
        every { uiUtils.readNonBlankInputOrNull(reader) } returns "OldTask" andThen null
        coEvery { manageTasksUseCase.getTaskByName("OldTask") } returns oldTask
        coEvery { manageStateUseCase.getEntityStateIdByName(updatedStateName) } returns stateId1
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk(relaxed = true)
        coEvery { manageTasksUseCase.updateTask(any()) } returns true
        coEvery {
            auditServicesUseCase.addAuditForUpdateEntity(
                entityType = EntityType.TASK,
                existEntityName = oldTask.title,
                newEntityName = updatedName,
                entityId = oldTask.id,
                newDescription = updatedDescription,
                newStateName = updatedStateName,
                additionalInfo = projectName
            )
        } just Runs

        // When
        taskManagerUi.updateTask()

        // Then
        coVerify {
            printer.showMessageLine(UiMessages.PROJECT_NAME_PROMPT)
            reader.readStringOrNull()
            getProjectsUseCase.getProjectByName(projectName)
            printer.showMessageLine(UiMessages.TASK_NAME_PROMPT)
            uiUtils.readNonBlankInputOrNull(reader)
            manageTasksUseCase.getTaskByName("OldTask")
            printer.showMessageLine(UiMessages.TASK_NAME_PROMPT)
            reader.readStringOrNull()
            printer.showMessageLine(UiMessages.TASK_DESCRIPTION_PROMPT)
            reader.readStringOrNull()
            printer.showMessageLine(UiMessages.TASK_STATE_PROMPT)
            reader.readStringOrNull()
            manageStateUseCase.getEntityStateIdByName(updatedStateName)
            manageTasksUseCase.updateTask(any())
            auditServicesUseCase.addAuditForUpdateEntity(
                entityType = EntityType.TASK,
                existEntityName = oldTask.title,
                newEntityName = updatedName,
                entityId = oldTask.id,
                newDescription = updatedDescription,
                newStateName = updatedStateName,
                additionalInfo = projectName
            )
            printer.printTask(any())
        }
    }

    @Test
    fun `addTask() should print EMPTY_TASK_INPUT and return when readCreateTaskInput returns null`() = runBlocking {
        // Given
        val projectName = "MyProject"
        every { reader.readStringOrNull() } returnsMany listOf(projectName, null) // simulate task input null
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk(relaxed = true)

        // When
        taskManagerUi.addTask(null)

        // Then
        verify { printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT) }
        coVerify(exactly = 0) { manageTasksUseCase.addTask(any(), any()) }
    }

    @Test
    fun `addTask() should show error message and return when getEntityStateIdByName throws`() = runBlocking {
        // Given
        val projectName = "MyProject"
        val taskInput = TaskInput("taskName", "desc", "stateName")

        every { reader.readStringOrNull() } returnsMany listOf(
            projectName,
            taskInput.name,
            taskInput.description,
            taskInput.stateName
        )
        coEvery { manageStateUseCase.getEntityStateIdByName(taskInput.stateName) } throws Exception("State error")

        // When
        taskManagerUi.addTask(null)

        // Then
        verify { printer.showMessageLine("State error") }
        coVerify(exactly = 0) { manageTasksUseCase.addTask(any(), any()) }
    }

    @Test
    fun `addTask() should show error message when exception thrown during adding task or audit`() = runBlocking {
        // Given
        val projectName = "MyProject"
        val taskInput = TaskInput("taskName", "desc", "stateName")
        val stateId1 = UUID.randomUUID()
        val projectId = UUID.randomUUID()

        every { reader.readStringOrNull() } returnsMany listOf(
            projectName,
            taskInput.name,
            taskInput.description,
            taskInput.stateName
        )
        coEvery { manageStateUseCase.getEntityStateIdByName(taskInput.stateName) } returns stateId1
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk(relaxed = true) {
            every { id } returns projectId
            every { title } returns projectName
        }
        coEvery { manageTasksUseCase.addTask(any(), projectId) } throws Exception("Add task failed")

        // When
        taskManagerUi.addTask(null)

        // Then
        verify { printer.showMessageLine("Add task failed") }
    }

    @Test
    fun `updateTask() should return early and print message when update input is null`() = runTest {
        // Given
        val taskName = "TaskX"
        val projectName = "Project1"
        val existingTask = buildTask(UUID.randomUUID(), taskName)

        every { reader.readStringOrNull() } returnsMany listOf(projectName, taskName, null)
        coEvery { manageTasksUseCase.getTaskByName(taskName) } returns existingTask
        every { printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT) } just Runs

        // When
        taskManagerUi.updateTask()

        // Then
        verify { printer.showMessageLine(UiMessages.EMPTY_TASK_INPUT) }
        coVerify(exactly = 0) { manageTasksUseCase.updateTask(any()) }
        verify(exactly = 0) { printer.printTask(any()) }
    }


    @Test
    fun `deleteTask() should delete task and print success when inputs are valid`() = runBlocking {
        // Given
        val projectName = "MyProject"
        val taskName = "TaskToDelete"
        val task = buildTask(taskId1, taskName)

        every { reader.readStringOrNull() } returns projectName
        every { uiUtils.readNonBlankInputOrNull(reader) } returns taskName

        coEvery { manageTasksUseCase.getTaskByName(taskName) } returns task
        coEvery { manageTasksUseCase.deleteTaskByName(taskName) } returns true
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk(relaxed = true) {
            every { id } returns projectId
        }
        coEvery { manageTasksUseCase.deleteTaskFromProject(projectId, task.id) } returns true
        coEvery {
            auditServicesUseCase.addAuditForDeleteEntity(
                EntityType.TASK,
                taskName,
                task.id,
                projectName
            )
        } just Runs

        // When
        taskManagerUi.deleteTask()

        // Then
        coVerifySequence {
            printer.showMessageLine(UiMessages.PROJECT_NAME_PROMPT)
            getProjectsUseCase.getProjectByName(projectName)
            printer.showMessageLine(UiMessages.TASK_NAME_PROMPT)
            uiUtils.readNonBlankInputOrNull(reader)
            manageTasksUseCase.getTaskByName(taskName)
            manageTasksUseCase.deleteTaskByName(taskName)
            getProjectsUseCase.getProjectByName(projectName)
            manageTasksUseCase.deleteTaskFromProject(projectId, task.id)
            auditServicesUseCase.addAuditForDeleteEntity(EntityType.TASK, taskName, task.id, projectName)
            printer.showMessageLine(UiMessages.TASK_DELETE_SUCCESSFULLY)
        }
    }

    @Test
    fun `showAllTasksInProject() should return tasks and print them when successful`() {
        // Given
        val tasks = listOf(
            buildTask(UUID.randomUUID(), "Task1"),
            buildTask(UUID.randomUUID(), "Task2")
        )
        val projectName = "ProjectX"

        val spyUi = spyk(taskManagerUi, recordPrivateCalls = true)

        every { spyUi["getProjectByName"]() } returns projectName
        coEvery { manageTasksUseCase.getTasksInProjectByName(projectName) } returns tasks
        every { printer.printTaskList(tasks) } just Runs

        // When
        val result = spyUi.showAllTasksInProject()

        // Then
        assertThat(result).isEqualTo(tasks)
        verify { printer.printTaskList(tasks) }
    }

    @Test
    fun `showAllTasksInProject() should return emptyList and print error when exception occurs`() {
        // Given
        val exception = RuntimeException("Test error")
        val spyUi = spyk(taskManagerUi, recordPrivateCalls = true)

        every { spyUi["getProjectByName"]() } throws exception
        every { printer.showMessageLine("Test error") } just Runs

        // When
        val result = spyUi.showAllTasksInProject()

        // Then
        assertThat(result).isEmpty()
        verify { printer.showMessageLine("Test error") }
    }

    @Test
    fun `launchUi() when current user is null then prints invalid user message and returns`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns null

        // When
        taskManagerUi.launchUi()

        // Then
        verify { printer.showMessageLine(UiMessages.INVALID_USER) }
        verify(exactly = 0) { reader.readIntOrNull() }
    }

    @Test
    fun `launchUi() when current user exists then loops until enteredTaskOption returns true`() {
        // Given
        every { loginUseCase.getCurrentUser() } returns mockk()
        every { printer.showMessageLine(any()) } just Runs
        every { printer.showMessage(any()) } just Runs
        every { reader.readIntOrNull() } returnsMany listOf(1, 0)

        // When
        taskManagerUi.launchUi()

        // Then
        verify(atLeast = 2) { printer.showMessageLine(UiMessages.SHOW_TASK_MANAGEMENT_OPTIONS) }
        verify(atLeast = 2) { printer.showMessage(UiMessages.SELECT_OPTION) }
        verify(exactly = 2) { reader.readIntOrNull() }
    }

    @Test
    fun `launchUi() exits cleanly when option 0 is selected`() = runTest {
        // Given
        every { reader.readIntOrNull() } returns 0

        // When
        taskManagerUi.launchUi()

        // Then
        verify(exactly = 1) { reader.readIntOrNull() }
    }

    @Test
    fun `launchUi() calls showAllTasks when option 1 is selected`() = runTest {
        // Given
        every { reader.readIntOrNull() } returns 1 andThen 0
        coEvery { taskManagerUi.showAllTasks() } just Runs

        // When
        taskManagerUi.launchUi()

        // Then
        coVerify(exactly = 1) { taskManagerUi.showAllTasks() }
    }

    @Test
    fun `launchUi() calls getTaskByName when option 2 is selected`() = runTest {
        // Given
        every { reader.readIntOrNull() } returns 2 andThen 0
        coEvery { taskManagerUi.getTaskByName() } just Runs

        // When
        taskManagerUi.launchUi()

        // Then
        coVerify(exactly = 1) { taskManagerUi.getTaskByName() }
    }

    @Test
    fun `launchUi() calls showAllMateTaskAssignment when option 7 is selected`() = runTest {
        // Given
        every { reader.readIntOrNull() } returns 7 andThen 0
        coEvery { taskManagerUi.showAllMateTaskAssignment() } just Runs

        // When
        taskManagerUi.launchUi()

        // Then
        coVerify(exactly = 1) { taskManagerUi.showAllMateTaskAssignment() }
    }

}