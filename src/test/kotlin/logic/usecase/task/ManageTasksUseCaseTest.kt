package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.*
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildTask
import java.util.*
import kotlin.test.assertTrue

class ManageTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    val projectId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
    val task = buildTask(title = "Task1")

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
    }

    @Test
    fun `getAllTasks() should return tasks when list is not empty`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns listOf(task)

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first()).isEqualTo(task)
    }

    @Test
    fun `getAllTasks() should throw TasksNotFoundException when list is empty`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns emptyList()

        // When & Then
        assertThrows<TasksNotFoundException> {
            manageTasksUseCase.getAllTasks()
        }
    }

    @Test
    fun `getTaskByName() should return task when name matches`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns listOf(task)

        // When
        val result = manageTasksUseCase.getTaskByName(task.title)

        // Then
        assertThat(result).isEqualTo(task)
    }

    @Test
    fun `getTaskByName() should throw TaskNotFoundException when no name matches`() = runTest {
        // Given
        val taskName = "Task2"
        coEvery { taskRepository.getAllTasks() } returns listOf(task)

        // When & Then
        assertThrows<TaskNotFoundException> {
            manageTasksUseCase.getTaskByName(taskName)
        }
    }

    @Test
    fun `getTaskIdByName() should return task ID when task exists`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns listOf(task)

        // When
        val result = manageTasksUseCase.getTaskIdByName(task.title)

        // Then
        assertThat(result).isEqualTo(task.id)
    }


    @Test
    fun `addTask() should return true when task is added`() = runTest {
        // Given
        coEvery { taskRepository.getTasksInProject(projectId) } returns emptyList()
        coEvery { taskRepository.addTask(task) } returns true

        // When
        val result = manageTasksUseCase.addTask(task, projectId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `addTask() should throw DuplicateTaskNameException if task already exists in project`() = runTest {
        // Given
        coEvery { taskRepository.getTasksInProject(projectId) } returns listOf(task)

        // When & Then
        assertThrows<DuplicateTaskNameException> {
            manageTasksUseCase.addTask(task, projectId)
        }
    }

    @Test
    fun `addTask() should throw TaskNotAddedException when add fails`() = runTest {
        // Given
        coEvery { taskRepository.getTasksInProject(projectId) } returns emptyList()
        coEvery { taskRepository.addTask(task) } returns false

        // When & Then
        assertThrows<TaskNotAddedException> {
            manageTasksUseCase.addTask(task, projectId)
        }
    }

    @Test
    fun `updateTask() should return true when updated successfully`() = runTest {
        // Given
        coEvery { taskRepository.updateTask(task) } returns true

        // When
        val result = manageTasksUseCase.updateTask(task)

        // Then
        assertTrue(result)
    }

    @Test
    fun `updateTask() should throw TaskNotUpdatedException when update fails`() = runTest {
        // Given
        coEvery { taskRepository.updateTask(task) } returns false

        // When & Then
        assertThrows<TaskNotUpdatedException> {
            manageTasksUseCase.updateTask(task)
        }
    }

    @Test
    fun `deleteTaskByName() should return true when deleted successfully`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns listOf(task)
        coEvery { taskRepository.deleteTask(task.id) } returns true

        // When
        val result = manageTasksUseCase.deleteTaskByName(task.title)

        // Then
        assertTrue(result)
    }

    @Test
    fun `deleteTaskByName() should throw TaskNotDeletedException when delete fails`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns listOf(task)
        coEvery { taskRepository.deleteTask(task.id) } returns false

        // When & Then
        assertThrows<TaskNotDeletedException> {
            manageTasksUseCase.deleteTaskByName(task.title)
        }
    }
}
