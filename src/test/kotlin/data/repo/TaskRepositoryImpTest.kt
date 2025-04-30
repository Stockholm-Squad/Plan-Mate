package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import logic.model.entities.Task
import org.example.data.datasources.PlanMateDataSource
import org.example.data.entities.MateTaskAssignment
import org.example.data.entities.TaskInProject
import org.example.data.repo.TaskRepositoryImp
import org.example.logic.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskRepositoryImpTest {

    private lateinit var taskDataSource: PlanMateDataSource<Task>
    private lateinit var taskInProjectDataSource: PlanMateDataSource<TaskInProject>
    private lateinit var mateTaskAssignmentCsvDataSource: PlanMateDataSource<MateTaskAssignment>
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setUp() {
        taskDataSource = mockk(relaxed = true)
        taskInProjectDataSource = mockk(relaxed = true)
        mateTaskAssignmentCsvDataSource = mockk(relaxed = true)
        taskRepository = TaskRepositoryImp(
            taskDataSource,
            taskInProjectDataSource,
            mateTaskAssignmentCsvDataSource
        )
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        // Given
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(taskList)

        // When
        val result = taskRepository.getAllTasks()

        // Then
        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getAllTasks() should return failure result with throwable when error happens while reading from the file`() {
        // Given
        every { taskDataSource.read("tasks.csv") } returns Result.failure(Throwable("File read error"))

        // When
        val result = taskRepository.getAllTasks()

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }
  
    @Test
    fun `createTask() should return success result with true when the task is created successfully`() {
        // Given
        val newTask = Task(
            id = "3",
            name = "Task 3",
            description = "Description 3",
            stateId = "state3",
            createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0),
            updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(existingTasks)
        every { taskDataSource.write(any()) } returns Result.success(true)

        // When
        val result = taskRepository.createTask(newTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `createTask() should return failure result with throwable when the task already exists`() {
        // Given
        val newTask = Task(
            id = "1",
            name = "Task 1",
            description = "Description 1",
            stateId = "state1",
            createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0),
            updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(existingTasks)

        // When
        val result = taskRepository.createTask(newTask)

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editTask() should return success result with true when the task is updated successfully`() {
        // Given
        val updatedTask = Task(
            id = "1",
            name = "Updated Task",
            description = "Updated Description",
            stateId = "state2",
            createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0),
            updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(existingTasks)
        every { taskDataSource.write(any()) } returns Result.success(true)

        // When
        val result = taskRepository.editTask(updatedTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editTask() should return failure result with throwable when the task to edit does not exist`() {
        // Given
        val updatedTask = Task(
            id = "999",
            name = "Updated Task",
            description = "Updated Description",
            stateId = "state2",
            createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0),
            updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0)
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(existingTasks)

        // When
        val result = taskRepository.editTask(updatedTask)

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteTask() should return success result with true when the task is deleted successfully`() {
        // Given
        val taskId = "1"
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(existingTasks)
        every { taskDataSource.write(any()) } returns Result.success(true)

        // When
        val result = taskRepository.deleteTask(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteTask() should return failure result with throwable when the task to delete does not exist`() {
        // Given
        val taskId = "999"
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = LocalDateTime(2025, 4, 29, 12, 0, 0), updatedDate = LocalDateTime(2025, 4, 29, 12, 0, 0))
        )
        every { taskDataSource.read("tasks.csv") } returns Result.success(existingTasks)

        // When
        val result = taskRepository.deleteTask(taskId)

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `getAllTasksByProjectId() should return success result with tasks filtered by project ID`() {
        // Given
        val projectId = "project1"
        val taskInProjectList = listOf(
            TaskInProject(taskId = "1", projectId = "project1"),
            TaskInProject(taskId = "2", projectId = "project2")
        )
        every { taskInProjectDataSource.read("tasks_in_projects.csv") } returns Result.success(taskInProjectList)

        // When
        val result = taskRepository.getAllTasksByProjectId(projectId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(listOf(TaskInProject(taskId = "1", projectId = "project1")))
    }

    @Test
    fun `getAllTasksByProjectId() should return failure result with throwable when no tasks are found for the project ID`() {
        // Given
        val projectId = "project999"
        val taskInProjectList = listOf(
            TaskInProject(taskId = "1", projectId = "project1"),
            TaskInProject(taskId = "2", projectId = "project2")
        )
        every { taskInProjectDataSource.read("tasks_in_projects.csv") } returns Result.success(taskInProjectList)

        // When
        val result = taskRepository.getAllTasksByProjectId(projectId)

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `getAllTasksByUserId() should return success result with tasks filtered by user ID`() {
        // Given
        val userId = "user1"
        val mateTaskAssignmentLists = listOf(
            MateTaskAssignment(user = "user1", taskId = "1"),
            MateTaskAssignment(user = "user2", taskId = "2")
        )
        every { mateTaskAssignmentCsvDataSource.read("users_assigned_to_tasks.csv") } returns Result.success(mateTaskAssignmentLists)

        // When
        val result = taskRepository.getAllMateTaskAssignment(userId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(listOf(MateTaskAssignment(user = "user1", taskId = "1")))
    }

    @Test
    fun `getAllTasksByUserId() should return failure result with throwable when no tasks are found for the user ID`() {
        // Given
        val userId = "user999"
        val mateTaskAssignmentLists = listOf(
            MateTaskAssignment(user = "user1", taskId = "1"),
            MateTaskAssignment(user = "user2", taskId = "2")
        )
        every { mateTaskAssignmentCsvDataSource.read("users_assigned_to_tasks.csv") } returns Result.success(mateTaskAssignmentLists)

        // When
        val result = taskRepository.getAllMateTaskAssignment(userId)

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }
}