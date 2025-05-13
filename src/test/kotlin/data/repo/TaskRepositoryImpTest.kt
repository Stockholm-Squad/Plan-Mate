package data.repo

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.repo.TaskRepositoryImp
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.TaskDataSource
import org.example.data.source.TaskInProjectDataSource
import org.example.logic.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import org.example.data.mapper.mapToTaskModel
import org.example.logic.TaskNotAddedException
import org.example.logic.TaskNotDeletedException
import org.example.logic.TaskNotUpdatedException
import org.example.logic.TasksNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TaskRepositoryImpTest {
    private lateinit var taskDataSource: TaskDataSource
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setUp() {
        taskDataSource = mockk(relaxed = true)
        taskRepository = TaskRepositoryImp(
            taskDataSource = taskDataSource,
        )
    }

    @Test
    fun `getAllTasks() should return list of tasks when datasource success`() = runTest {
        //Given
        coEvery { taskDataSource.getAllTasks() } returns taskDtoList
        //When
        val result = taskRepository.getAllTasks()
        //Then
        assertThat(result).isEqualTo(tasksList)

    }

    @Test
    fun `getAllTasks() should return exception when datasource fails`() = runTest {
        //Given
        coEvery { taskDataSource.getAllTasks() } throws Exception()
        //When&Then
        assertThrows<TasksNotFoundException> {
            taskRepository.getAllTasks()
        }
    }

    @Test
    fun `addTask() should return true when datasource adds task successfully`() = runTest {
        // Given

        coEvery { taskDataSource.addTask(taskDto) } returns true

        // When
        val result = taskRepository.addTask(task)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addTask() should return false when datasource returns false`() = runTest {
        // Given
        val task = tasksList[1]
        coEvery { taskDataSource.addTask(taskDto) } returns false

        // When
        val result = taskRepository.addTask(task)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addTask() should throw TaskNotAddedException when datasource throws exception`() = runTest {
        // Given
        coEvery { taskDataSource.addTask(taskDto) } throws Exception()

        // When & Then
        assertThrows<TaskNotAddedException> {
            taskRepository.addTask(task)
        }
    }

    @Test
    fun `updateTask() should return true when datasource updates task successfully`() = runTest {
        // Given
        val task = tasksList[0]
        coEvery { taskDataSource.updateTask(taskDto) } returns true

        // When
        val result = taskRepository.updateTask(task)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `updateTask() should return false when datasource returns false`() = runTest {
        // Given
        coEvery { taskDataSource.updateTask(taskDto) } returns false

        // When
        val result = taskRepository.updateTask(task)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `updateTask() should throw TaskNotUpdatedException when datasource throws exception`() = runTest {
        // Given
        coEvery { taskDataSource.updateTask(taskDto) } throws Exception()

        // When & Then
        assertThrows<TaskNotUpdatedException> {
            taskRepository.updateTask(task)
        }
    }

    @Test
    fun `deleteTask() should return true when task is deleted successfully`() = runTest {
        coEvery { taskDataSource.deleteTask(taskDto.id) } returns true

        val result = taskRepository.deleteTask(task.id)

        assertThat(result).isTrue()
    }

    @Test
    fun `deleteTask() should return false when deletion fails`() = runTest {

        coEvery { taskDataSource.deleteTask(taskDto.id) } returns false

        val result = taskRepository.deleteTask(task.id)

        assertThat(result).isFalse()
    }

    @Test
    fun `deleteTask() should throw TaskNotDeletedException on error`() = runTest {
        coEvery { taskDataSource.deleteTask(taskDto.id) } throws Exception()

        assertThrows<TaskNotDeletedException> {
            taskRepository.deleteTask(task.id)
        }
    }

    @Test
    fun `getTasksInProject() should return tasks list when successful`() = runTest {
        val projectId = UUID.randomUUID()
        coEvery { taskDataSource.getTasksInProject(projectId.toString()) } returns taskDtoList

        val result = taskRepository.getTasksInProject(projectId)

        assertThat(result).isEqualTo(tasksList)
    }

    @Test
    fun `getTasksInProject() should throw TasksNotFoundException on failure`() = runTest {
        val projectId = UUID.randomUUID()
        coEvery { taskDataSource.getTasksInProject(projectId.toString()) } throws Exception()

        assertThrows<TasksNotFoundException> {
            taskRepository.getTasksInProject(projectId)
        }
    }

    @Test
    fun `addTaskInProject() should return true when added successfully`() = runTest {
        val projectId = UUID.randomUUID()
        coEvery { taskDataSource.addTaskInProject(projectId.toString(), taskDto.id) } returns true

        val result = taskRepository.addTaskInProject(projectId, task.id)

        assertThat(result).isTrue()
    }

    @Test
    fun `addTaskInProject() should return false when not added`() = runTest {
        val projectId = UUID.randomUUID()
        coEvery { taskDataSource.addTaskInProject(projectId.toString(), taskDto.id) } returns false

        val result = taskRepository.addTaskInProject(projectId, task.id)

        assertThat(result).isFalse()
    }

    @Test
    fun `addTaskInProject() should throw TaskNotAddedException on failure`() = runTest {
        val projectId = UUID.randomUUID()
        coEvery { taskDataSource.addTaskInProject(projectId.toString(), taskDto.id) } throws Exception()

        assertThrows<TaskNotAddedException> {
            taskRepository.addTaskInProject(projectId, task.id)
        }
    }
    @Test
    fun `deleteTaskFromProject() should return true when deleted successfully`() = runTest {
        val projectId = UUID.randomUUID()

        coEvery { taskDataSource.deleteTaskFromProject(projectId.toString(),taskDto.id) } returns true

        val result = taskRepository.deleteTaskFromProject(projectId, task.id)

        assertThat(result).isTrue()
    }

    @Test
    fun `deleteTaskFromProject() should return false when deletion failed`() = runTest {
        val projectId = UUID.randomUUID()

        coEvery { taskDataSource.deleteTaskFromProject(projectId.toString(), taskDto.id) } returns false

        val result = taskRepository.deleteTaskFromProject(projectId, task.id)

        assertThat(result).isFalse()
    }
    @Test
    fun `deleteTaskFromProject() should throw TaskNotDeletedException on failure`() = runTest {
        val projectId = UUID.randomUUID()
        coEvery { taskDataSource.deleteTaskFromProject(projectId.toString(), taskDto.id) } throws Exception()

        assertThrows<TaskNotDeletedException> {
            taskRepository.deleteTaskFromProject(projectId, task.id)
        }
    }

        @Test
        fun `getAllTasksByUserName() should return list when successful`() = runTest {
            val username = "username"
            coEvery { taskDataSource.getAllTasksByUserName(username) } returns taskDtoList

            val result = taskRepository.getAllTasksByUserName(username)

            assertThat(result).isEqualTo(tasksList)
        }

        @Test
        fun `getAllTasksByUserName() should throw TasksNotFoundException on failure`() = runTest {
            val username = "username"
            coEvery { taskDataSource.getAllTasksByUserName(username) } throws Exception()

            assertThrows<TasksNotFoundException> {
                taskRepository.getAllTasksByUserName(username)
            }
        }
}