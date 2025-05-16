package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import io.kotest.common.runBlocking
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import logic.usecase.audit.taskName
import org.example.logic.*
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import utils.buildTask
import java.util.*
import kotlin.test.assertTrue

class ManageTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageTasksUseCase: ManageTasksUseCase

    val projectId: UUID = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
    private val taskId = UUID.randomUUID()

    val task = buildTask(title = "Task1")
    private val testTask = buildTask(
        id = taskId,
        title = "Test Task",
        stateId = UUID.randomUUID(),
        projectName = "plan-mate",
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        manageTasksUseCase = ManageTasksUseCase(taskRepository, getProjectsUseCase)
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
    fun `getAllTasks() should throw TasksNotFoundException when list is empty`() {
        val scope = CoroutineScope(Dispatchers.Default)

        // Given
        coEvery { taskRepository.getAllTasks() } returns emptyList()

        // When
        val deferred = scope.async {
            manageTasksUseCase.getAllTasks()
        }

        // Then
        assertThrows<TasksNotFoundException> {
            runBlocking {
                deferred.await()
            }
        }
    }

    @Test
    fun `getAllTasks() should return list of tasks when not empty`() = runTest {
        // Given
        val tasks = listOf(buildTask(projectId), buildTask(taskId))
        coEvery { taskRepository.getAllTasks() } returns tasks

        // When
        val result = manageTasksUseCase.getAllTasks()

        // Then
        assertThat(result).isEqualTo(tasks)
    }

    @Test
    fun `getAllTasks() should throw TasksNotFoundException when listt is empty`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns emptyList()

        // When & Then
        assertThrows<TasksNotFoundException> {
            runBlocking {
                manageTasksUseCase.getAllTasks()
            }
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
    fun `getTaskIdByName() should throw TaskNotFoundException when task does not exist`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns emptyList()

        // When & Throw
        assertThrows<TaskNotFoundException> {
            manageTasksUseCase.getTaskIdByName(taskName)
        }
    }

    @Test
    fun `getTaskIdByName() should throw TaskNotFoundException when name does not match`() = runTest {
        // Given
        coEvery { taskRepository.getAllTasks() } returns listOf(task)

        // When & Throw
        assertThrows<TaskNotFoundException> {
            manageTasksUseCase.getTaskIdByName("NotInList")
        }
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

    @Test
    fun `getTasksAssignedToProject() should return tasks when successful`() = runTest {
        // Given
        coEvery { taskRepository.getTasksInProject(projectId) } returns listOf(testTask)
        // When
        val result = manageTasksUseCase.getTasksInProjectById(projectId)
        // Then
        assertThat(result).containsExactly(testTask)
    }

    @Test
    fun `getTasksAssignedToProject() should return project repository failure`() = runTest {
        // Given
        coEvery { taskRepository.getTasksInProject(projectId) } throws ReadDataException()

        // When & Then
        assertThrows<ReadDataException> {
            manageTasksUseCase.getTasksInProjectById(projectId)
        }
    }

    @Test
    fun `getTasksAssignedToProject() should throw TasksNotFoundException when no tasks`() = runTest {
        // Given
        coEvery { taskRepository.getTasksInProject(projectId) } returns emptyList()
        assertThrows<TasksNotFoundException> {
            manageTasksUseCase.getTasksInProjectById(projectId)
        }
    }

    @Test
    fun `addTaskAssignedToProject() should return true when repository succeeds`() = runTest {
        // Given
        coEvery { taskRepository.addTaskInProject(projectId, taskId) } returns true
        // When
        val result = manageTasksUseCase.addTaskToProject(projectId, taskId)
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addTaskAssignedToProject() should return failure`() = runTest {
        // Given
        coEvery { taskRepository.addTaskInProject(projectId, taskId) } throws ReadDataException()

        // When & Then
        assertThrows<ReadDataException> {
            manageTasksUseCase.addTaskToProject(projectId, taskId)
        }
    }

    @Test
    fun `addTaskAssignedToProject() should throw TaskNotAddedException when repository returns false`() = runTest {
        // Given
        coEvery { taskRepository.addTaskInProject(projectId, taskId) } returns false
        assertThrows<TaskNotAddedException> {
            manageTasksUseCase.addTaskToProject(projectId, taskId)
        }
    }


    @Test
    fun `deleteTaskAssignedToProject() should return true when task exists and deletion succeeds`() = runTest {
        // Given
        coEvery { taskRepository.deleteTaskFromProject(projectId, taskId) } returns true

        // When
        val result = manageTasksUseCase.deleteTaskFromProject(projectId, taskId)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `deleteTaskAssignedToProject() should return delete failure`() = runTest {
        // Given
        coEvery { taskRepository.deleteTaskFromProject(projectId, taskId) } throws ReadDataException()

        // When & Then
        assertThrows<ReadDataException> {
            manageTasksUseCase.deleteTaskFromProject(projectId, taskId)
        }
    }

    @Test
    fun `deleteTaskAssignedToProject() should throw TaskNotDeletedException when repository returns false`() =
        runTest {
            // Given
            coEvery { taskRepository.deleteTaskFromProject(projectId, taskId) } returns false

            // When & Then
            assertThrows<TaskNotDeletedException> {
                manageTasksUseCase.deleteTaskFromProject(projectId, taskId)
            }
        }


    @Test
    fun `getTasksInProjectByName() should return tasks when project found and tasks exist`() = runTest {
        // Given
        val projectName = "Project"
        val project = buildProject(name = projectName)
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
        coEvery { taskRepository.getTasksInProject(project.id) } returns listOf(testTask)
        // When
        val result = manageTasksUseCase.getTasksInProjectByName(projectName)

        // Then
        assertThat(result).containsExactly(testTask)
        coVerify { taskRepository.getTasksInProject(project.id) }
    }

    @Test
    fun `getTasksInProjectByName() should throw TasksNotFoundException when no tasks`() = runTest {
        // Given
        val projectName = "Project"
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns buildProject(name = projectName)
        coEvery { taskRepository.getTasksInProject(testTask.id) } returns emptyList()

        // When & Then
        assertThrows<TasksNotFoundException> {
            manageTasksUseCase.getTasksInProjectByName(projectName)
        }
    }

    @Test
    fun `getTasksInProjectByName() should return exception when getProjectByName fails`() = runTest {
        // Given
        val projectName = "Proj"
        coEvery { getProjectsUseCase.getProjectByName(projectName) } throws ReadDataException()

        // When & Then
        assertThrows<ReadDataException> {
            manageTasksUseCase.getTasksInProjectByName(projectName)
        }
    }

    @Test
    fun `getAllTasksByUserName() should return tasks when tasks exist`() = runTest {
        // Given
        val userName = "user1"
        coEvery { taskRepository.getAllTasksByUserName(userName) } returns listOf(testTask)

        // When
        val result = manageTasksUseCase.getAllTasksByUserName(userName)

        // Then
        assertThat(result).containsExactly(testTask)
    }

    @Test
    fun `getAllTasksByUserName() should throw TasksNotFoundException when no tasks for user`() = runTest {
        // Given
        val userName = "user2"
        coEvery { taskRepository.getAllTasksByUserName(userName) } returns emptyList()

        // When & Then
        assertThrows<TasksNotFoundException> {
            manageTasksUseCase.getAllTasksByUserName(userName)
        }
    }

    @Test
    fun `getAllTasksByUserName() should return repository failure`() = runTest {
        // Given
        val userName = "user3"
        coEvery { taskRepository.getAllTasksByUserName(userName) } throws ReadDataException()

        // When & Then
        assertThrows<ReadDataException> {
            manageTasksUseCase.getAllTasksByUserName(userName)
        }
    }
}

