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
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.utils.DateHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ManageTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var dataHandler: DateHandler

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
        dataHandler= DateHandler()
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        // Given
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime()),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
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
        every { taskRepository.getAllTasks() } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThrows<PlanMateExceptions.LogicException.NoTasksFound> { result.getOrThrow() }
    }

    @Test
    fun `getTaskById() should return success result with the task when the task is found`() {
        // Given
        val taskId = "1"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime()),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
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
        every { taskRepository.getAllTasks() } returns Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException())
        // When
        val result=manageTasksUseCase.getTaskById(taskId)
        // Then
        assertThrows <PlanMateExceptions.LogicException.TaskNotFoundException>{ result.getOrThrow() }
    }

    @Test
    fun `createTask() should return true result when the task created`() {
        // Given
        val task = Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        every { taskRepository.createTask(task) } returns Result.success(true)
        // When
        val result = manageTasksUseCase.createTask(task)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `createTask() should return failure result when the task already exists`() {
        // Given
        val task = Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        every { taskRepository.createTask(task) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksCreated())
        // When
        val result = manageTasksUseCase.createTask(task)

        // Then
        assertThrows <PlanMateExceptions.LogicException.NoTasksCreated>{
            result.getOrThrow()
        }
    }

    @Test
    fun `editTask() should return success result when the task is successfully updated`() {
        // Given
        val updatedTask = Task(id = "1", name = "Updated Task", description = "Updated Description", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        every { taskRepository.editTask(updatedTask) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editTask() should return failure result when the task cannot be updated`() {
        // Given
        val updatedTask = Task(id = "1", name = "Updated Task", description = "Updated Description", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        every { taskRepository.editTask(updatedTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        val result = manageTasksUseCase.editTask(updatedTask)

        // Then
        assertThrows<PlanMateExceptions.LogicException.NoTasksFound> {
            result.getOrThrow()
        }
    }

    @Test
    fun `deleteTask() should return success result when the task is successfully deleted`() {
        // Given
        val taskId = "1"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)
        every { taskRepository.deleteTask(taskId) } returns Result.success(true)

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteTask() should return failure result when the task is not found`() {
        // Given
        val taskId = "3"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        // When


        // Then
        assertThrows <PlanMateExceptions.LogicException.NoTasksDeleted>{manageTasksUseCase.deleteTask(taskId) }
    }

    @Test
    fun `deleteTask() should return failure result when use case fails`() {
        // Given
        val taskId = "3"
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        )
        every { taskRepository.getAllTasks() } returns Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException())

        // When
        val result = manageTasksUseCase.deleteTask(taskId)

        // Then
        assertThrows <PlanMateExceptions.LogicException.TaskNotFoundException>{ result.getOrThrow() }
    }

//    @Test
//    fun `getAllMateTaskAssignment() should return success result with a list of mate task assignments`() {
//        // Given
//        val userName = "user1"
//        val assignments = listOf(
//            MateTaskAssignment(user = "user1", taskId = "1"),
//            MateTaskAssignment(user = "user1", taskId = "2")
//        )
//        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(assignments)
//
//        // When
//        val result = manageTasksUseCase.getAllMateTaskAssignment(userName)
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(assignments)
//    }
//
//    @Test
//    fun `getAllMateTaskAssignment() should return failure result when no assignments are found`() {
//        // Given
//        val userName = "user1"
//        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(emptyList<MateTaskAssignment>())
//
//        // When
//        val result = manageTasksUseCase.getAllMateTaskAssignment(userName)
//
//        // Then
//        assertThat(result.exceptionOrNull()?.message).isEqualTo("No tasks found for user 'user1'.")
//    }
//
//    @Test
//    fun `getAllTasksByProjectId() should return success result with tasks filtered by project ID`() {
//        // Given
//        val projectId = "project1"
//        val links = listOf(
//            TaskInProject(taskId = "1", projectId = "project1", stateId = "state1"),
//            TaskInProject(taskId = "2", projectId = "project2", stateId = "state2")
//        )
//        every { taskRepository.getAllTasksInProjects() } returns Result.success(links)
//
//        // When
//        val result = manageTasksUseCase.getAllTasksByProjectId(projectId)
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(links.filter { it.projectId == projectId })
//    }

//    @Test
//    fun `getAllTasksByProjectId() should return failure result when no tasks are found for the project`() {
//        // Given
//        val projectId = "project3"
//        val links = listOf(
//            TaskInProject(taskId = "1", projectId = "project1", stateId = "state1"),
//            TaskInProject(taskId = "2", projectId = "project2", stateId = "state2")
//        )
//        every { taskRepository.getAllTasksInProjects() } returns Result.success(links)
//
//        // When
//        val result = manageTasksUseCase.getAllTasksByProjectId(projectId)
//
//        // Then
//        assertThat(result.exceptionOrNull()?.message).isEqualTo("No tasks found for project 'project3'.")
//    }
}