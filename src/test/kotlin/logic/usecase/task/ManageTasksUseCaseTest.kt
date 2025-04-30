package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import org.example.logic.usecase.task.ManageTasksUseCase
import logic.model.entities.Task
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.logic.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ManageTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        // Given
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getTaskById() should return success result with the task when the task is found`() {
        // Given
        val taskId = "1"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When
        val result = manageTasksUseCase.getTaskById(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(taskList.find { it.id == taskId })
    }

    @Test
    fun `createTask() should return success result with true when the task is created successfully`() {
        // Given
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            name = "Task 3",
            description = "Description 3",
            stateId = "state3",
            createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0),
            updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)
        )

        every { taskRepository.createTask(any()) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.createTask(newTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editTask() should return success result with true when the task is updated successfully`() {
        // Given
        val taskId = "1"
        val updatedTask = Task(
            id = taskId,
            name = "Updated Task",
            description = "Updated Description",
            stateId = "state2",
            createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0),
            updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(existingTasks)
        every { taskRepository.editTask(any()) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteTask() should return success result with true when the task is deleted successfully`() {
        // Given
        val taskId = "1"
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(existingTasks)
        every { taskRepository.deleteTask(taskId) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `getAllTasksByProjectId() should return success result with tasks filtered by project ID`() {
        // Given
        val projectId = "project1"
        val taskInProjectList = listOf(
            TaskInProject(taskId = "1", projectId = "project1"),
            TaskInProject(taskId = "2", projectId = "project2")
        )
        every { taskRepository.getAllTasksByProjectId(projectId) } returns Result.success(taskInProjectList)

        // When
        val result = manageTasksUseCase.getAllTasksByProjectId(projectId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(listOf(TaskInProject(taskId = "1", projectId = "project1")))
    }

    @Test
    fun `getAllTasksByUserId() should return success result with tasks filtered by user ID`() {
        // Given
        val userId = "user1"
        val userAssignedToTaskList = listOf(
            MateTaskAssignment(user = "user1", taskId = "1"),
            MateTaskAssignment(user = "user2", taskId = "2")
        )
        every { taskRepository.getAllMateTaskAssignment(userId) } returns Result.success(userAssignedToTaskList)

        // When
        val result = manageTasksUseCase.getAllTasksByUserId(userId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(listOf(MateTaskAssignment(user = "user1", taskId = "1")))
    }
}