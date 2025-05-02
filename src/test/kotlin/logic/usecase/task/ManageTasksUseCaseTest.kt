package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import org.example.logic.usecase.task.ManageTasksUseCase
import logic.model.entities.Task
import org.example.logic.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import org.example.logic.model.exceptions.NoTasksCreated
import org.example.logic.model.exceptions.NoTasksFound
import org.example.logic.model.exceptions.TaskNotFoundException
import org.example.logic.model.exceptions.NoTasksDeleted
import org.example.utils.DateHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildTask

class ManageTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var dataHandler: DateHandler

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
        dataHandler = DateHandler()
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        // Given
        val taskList = listOf(
            Task(
                id = "1",
                name = "Task 1",
                description = "Description 1",
                stateId = "state1",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            ),
            Task(
                id = "2",
                name = "Task 2",
                description = "Description 2",
                stateId = "state2",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            )
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getAllTasks() should return failure result when no tasks are found`() {
        // Given
        every { taskRepository.getAllTasks() } returns Result.failure(NoTasksFound())

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThrows<NoTasksFound> { result.getOrThrow() }
    }

    @Test
    fun `getTaskById() should return success result with the task when the task is found`() {
        // Given
        val taskId = "1"
        val taskList = listOf(
            Task(
                id = "1",
                name = "Task 1",
                description = "Description 1",
                stateId = "state1",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            ),
            Task(
                id = "2",
                name = "Task 2",
                description = "Description 2",
                stateId = "state2",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            )
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When
        val result = manageTasksUseCase.getTaskById(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(taskList.find { it.id == taskId })
    }

    @Test
    fun `getTaskById() should return failure result when the task is not found`() {
        // Given
        val taskId = "3"
        every { taskRepository.getAllTasks() } returns Result.failure(TaskNotFoundException())
        // When
        val result = manageTasksUseCase.getTaskById(taskId)
        // Then
        assertThrows<TaskNotFoundException> { result.getOrThrow() }
    }

    @Test
    fun `createTask() should return true result when the task created`() {
        // Given
        val task = Task(
            id = "1",
            name = "Task 1",
            description = "Description 1",
            stateId = "state1",
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        every { taskRepository.createTask(task) } returns Result.success(true)
        // When
        val result = manageTasksUseCase.createTask(task)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `createTask() should return failure result when the task already exists`() {
        // Given
        val task = Task(
            id = "1",
            name = "Task 1",
            description = "Description 1",
            stateId = "state1",
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        every { taskRepository.createTask(task) } returns Result.failure(NoTasksCreated())
        // When
        val result = manageTasksUseCase.createTask(task)

        // Then
        assertThrows<NoTasksCreated> {
            result.getOrThrow()
        }
    }

    @Test
    fun `createTask() should return failure when repository returns false`() {
        val task = buildTask()
        every { taskRepository.createTask(task) } returns Result.failure(NoTasksCreated())

        val result = manageTasksUseCase.createTask(task)

        assertThrows<NoTasksCreated> { result.getOrThrow() }
    }


    @Test
    fun `editTask() should return success result when the task is successfully updated`() {
        // Given
        val updatedTask = Task(
            id = "1",
            name = "Updated Task",
            description = "Updated Description",
            stateId = "state1",
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        every { taskRepository.editTask(updatedTask) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editTask() should return failure result when the task cannot be updated`() {
        // Given
        val updatedTask = Task(
            id = "1",
            name = "Updated Task",
            description = "Updated Description",
            stateId = "state1",
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        every { taskRepository.editTask(updatedTask) } returns Result.failure(NoTasksFound())

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThrows<NoTasksFound> {
            result.getOrThrow()
        }
    }

    @Test
    fun `deleteTask() should return success result when the task is successfully deleted`() {
        // Given
        val taskId = "1"
        val taskList = listOf(
            Task(
                id = "1",
                name = "Task 1",
                description = "Description 1",
                stateId = "state1",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            )
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)
        every { taskRepository.deleteTask(taskId) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `showAllTasks should return tasks when found`() {
        // Given
        val sampleTasks = listOf(
            buildTask(name = "Task 1", description = "First task", stateId = "1"),
            buildTask(name = "Task 2", description = "Second task", stateId = "2")
        )
        every { manageTasksUseCase.getAllTasks() } returns Result.success(sampleTasks)

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThat(result.getOrNull()).isEqualTo(sampleTasks)
    }

    @Test
    fun `deleteTask() should return failure result when use case fails`() {
        // Given
        val taskId = "3"
        val taskList = listOf(
            Task(
                id = "1",
                name = "Task 1",
                description = "Description 1",
                stateId = "state1",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            )
        )
        every { taskRepository.getAllTasks() } returns Result.failure(TaskNotFoundException())

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThrows<TaskNotFoundException> { result.getOrThrow() }
    }

    @Test
    fun `deleteTask() should return failure result when task is not deleted even if repository call succeeds`() {
        // Given
        val taskId = "3"
        val taskList = listOf(
            Task(
                id = "3",
                name = "Task 1",
                description = "Description 1",
                stateId = "state1",
                createdDate = dataHandler.getCurrentDateTime(),
                updatedDate = dataHandler.getCurrentDateTime()
            )
        )

        every { taskRepository.getAllTasks() } returns Result.success(taskList)
        every { taskRepository.deleteTask(taskId) } returns Result.failure(NoTasksDeleted())


        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThrows<NoTasksDeleted> { result.getOrThrow() }
    }


}