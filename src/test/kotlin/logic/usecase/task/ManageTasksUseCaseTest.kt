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
    fun `getAllTasks() should return failure result when no tasks are found`() {
        // Given
        every { taskRepository.getAllTasks() } returns Result.success(emptyList<Task>())

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No tasks found.")
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
    fun `getTaskById() should return failure result when the task is not found`() {
        // Given
        val taskId = "3"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When
        val result = manageTasksUseCase.getTaskById(taskId)

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Task with ID '3' not found.")
    }

    @Test
    fun `createTask() should return success result when the task is successfully created and linked to a project`() {
        // Given
        val task = Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        val taskInProject = TaskInProject(taskId = "1", projectId = "project1", stateId = "state1")
        every { taskRepository.createTask(task) } returns Result.success(true)
        every { taskRepository.linkTaskToProject(taskInProject) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.createTask(task, taskInProject)

        // Then
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `createTask() should return failure result when the task already exists`() {
        // Given
        val task = Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        val taskInProject = TaskInProject(taskId = "1", projectId = "project1", stateId = "state1")
        every { taskRepository.createTask(task) } returns Result.failure(Exception("Task already exists"))

        // When
        val result = manageTasksUseCase.createTask(task, taskInProject)

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Task already exists")
    }

    @Test
    fun `editTask() should return success result when the task is successfully updated`() {
        // Given
        val updatedTask = Task(id = "1", name = "Updated Task", description = "Updated Description", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        every { taskRepository.editTask(updatedTask) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `editTask() should return failure result when the task cannot be updated`() {
        // Given
        val updatedTask = Task(id = "1", name = "Updated Task", description = "Updated Description", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        every { taskRepository.editTask(updatedTask) } returns Result.failure(Exception("Failed to update task"))

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Failed to update task")
    }

    @Test
    fun `deleteTask() should return success result when the task is successfully deleted`() {
        // Given
        val taskId = "1"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)
        every { taskRepository.deleteTask(taskId) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `deleteTask() should return failure result when the task is not found`() {
        // Given
        val taskId = "3"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Task with ID '3' not found.")
    }

    @Test
    fun `getAllMateTaskAssignment() should return success result with a list of mate task assignments`() {
        // Given
        val userName = "user1"
        val assignments = listOf(
            MateTaskAssignment(user = "user1", taskId = "1"),
            MateTaskAssignment(user = "user1", taskId = "2")
        )
        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(assignments)

        // When
        val result = manageTasksUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.getOrNull()).isEqualTo(assignments)
    }

    @Test
    fun `getAllMateTaskAssignment() should return failure result when no assignments are found`() {
        // Given
        val userName = "user1"
        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(emptyList<MateTaskAssignment>())

        // When
        val result = manageTasksUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No tasks found for user 'user1'.")
    }

    @Test
    fun `getAllTasksByProjectId() should return success result with tasks filtered by project ID`() {
        // Given
        val projectId = "project1"
        val links = listOf(
            TaskInProject(taskId = "1", projectId = "project1", stateId = "state1"),
            TaskInProject(taskId = "2", projectId = "project2", stateId = "state2")
        )
        every { taskRepository.getAllTasksInProjects() } returns Result.success(links)

        // When
        val result = manageTasksUseCase.getAllTasksByProjectId(projectId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(links.filter { it.projectId == projectId })
    }

    @Test
    fun `getAllTasksByProjectId() should return failure result when no tasks are found for the project`() {
        // Given
        val projectId = "project3"
        val links = listOf(
            TaskInProject(taskId = "1", projectId = "project1", stateId = "state1"),
            TaskInProject(taskId = "2", projectId = "project2", stateId = "state2")
        )
        every { taskRepository.getAllTasksInProjects() } returns Result.success(links)

        // When
        val result = manageTasksUseCase.getAllTasksByProjectId(projectId)

        // Then
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No tasks found for project 'project3'.")
    }
}