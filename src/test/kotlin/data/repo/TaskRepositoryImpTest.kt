package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.mapper.mapToTaskModel
import org.example.data.repo.TaskRepositoryImp
import org.example.data.source.TaskDataSource
import org.example.logic.TaskNotAddedException
import org.example.logic.TaskNotUpdatedException
import org.example.logic.TasksNotFoundException
import org.example.logic.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskRepositoryImpTest {
    private lateinit var taskDataSource: TaskDataSource
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setUp() {
        taskDataSource = mockk(relaxed = true)
        taskRepository = TaskRepositoryImp(
            taskDataSource = taskDataSource
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
        val task = tasksList[0]
        coEvery { taskDataSource.addTask(task.mapToTaskModel()) } returns true

        // When
        val result = taskRepository.addTask(task)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addTask() should return false when datasource returns false`() = runTest {
        // Given
        val task = tasksList[1]
        coEvery { taskDataSource.addTask(task.mapToTaskModel()) } returns false

        // When
        val result = taskRepository.addTask(task)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addTask() should throw TaskNotAddedException when datasource throws exception`() = runTest {
        // Given
        val task = tasksList[2]
        coEvery { taskDataSource.addTask(task.mapToTaskModel()) } throws Exception()

        // When & Then
        assertThrows<TaskNotAddedException> {
            taskRepository.addTask(task)
        }
    }

    @Test
    fun `updateTask() should return true when datasource updates task successfully`() = runTest {
        // Given
        val task = tasksList[0]
        coEvery { taskDataSource.updateTask(task.mapToTaskModel()) } returns true

        // When
        val result = taskRepository.updateTask(task)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `updateTask() should return false when datasource returns false`() = runTest {
        // Given
        val task = tasksList[1]
        coEvery { taskDataSource.updateTask(task.mapToTaskModel()) } returns false

        // When
        val result = taskRepository.updateTask(task)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `updateTask() should throw TaskNotUpdatedException when datasource throws exception`() = runTest {
        // Given
        val task = tasksList[2]
        coEvery { taskDataSource.updateTask(task.mapToTaskModel()) } throws Exception()

        // When & Then
        assertThrows<TaskNotUpdatedException> {
            taskRepository.updateTask(task)
        }
    }
}