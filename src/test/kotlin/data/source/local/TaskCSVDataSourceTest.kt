package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.MateTaskAssignmentDto
import data.dto.TaskDto
import data.dto.TaskInProjectDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.TaskDataSource
import org.example.data.source.TaskInProjectDataSource
import org.example.data.source.local.TaskCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildTaskModel

class TaskCSVDataSourceTest {

    private lateinit var taskReaderWriter: IReaderWriter<TaskDto>
    private lateinit var mateTaskAssignmentDataSource: MateTaskAssignmentDataSource
    private lateinit var taskInProjectDataSource: TaskInProjectDataSource
    private lateinit var dataSource: TaskDataSource

    private val task1 = buildTaskModel(id = "1", name = "Design")
    private val task2 = buildTaskModel(id = "2", name = "Development")
    private val updatedTask = buildTaskModel(id = "1", name = "UI Design")

    @BeforeEach
    fun setup() {
        taskReaderWriter = mockk(relaxed = true)
        mateTaskAssignmentDataSource = mockk(relaxed = true)
        taskInProjectDataSource = mockk(relaxed = true)
        dataSource = TaskCSVDataSource(taskReaderWriter, mateTaskAssignmentDataSource, taskInProjectDataSource)
    }

    @Test
    fun `getAllTasks should return all tasks`() = runTest {
        // Given
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)

        // When
        val result = dataSource.getAllTasks()

        // Then
        assertThat(result).containsExactly(task1, task2)
    }

    @Test
    fun `addTask should append a task and return true`() = runTest {
        // Given
        coEvery { taskReaderWriter.append(listOf(task1)) } returns true

        // When
        val result = dataSource.addTask(task1)

        // Then
        assertThat(result).isTrue()
        coVerify { taskReaderWriter.append(listOf(task1)) }
    }

    @Test
    fun `updateTask should overwrite with updated task`() = runTest {
        // Given
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)
        coEvery { taskReaderWriter.overWrite(any()) } returns true

        // When
        val result = dataSource.updateTask(updatedTask)

        // Then
        assertThat(result).isTrue()
        coVerify {
            taskReaderWriter.overWrite(
                match {
                    it.size == 2 &&
                            it.any { t -> t.id == "1" && t.title == "UI Design" } &&
                            it.any { t -> t.id == "2" && t.title == "Development" }
                }
            )
        }
    }

    @Test
    fun `deleteTask should overwrite with filtered tasks`() = runTest {
        // Given
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)
        coEvery { taskReaderWriter.overWrite(any()) } returns true

        // When
        val result = dataSource.deleteTask("1")

        // Then
        assertThat(result).isTrue()
        coVerify {
            taskReaderWriter.overWrite(match { it.size == 1 && it[0] == task2 })
        }
    }

    @Test
    fun `getTasksInProject should return tasks in a specific project`() = runTest {
        // Given
        coEvery {
            taskInProjectDataSource.getTasksInProjectByProjectId("p1")
        } returns listOf(TaskInProjectDto("1", "p1"))
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)

        // When
        val result = dataSource.getTasksInProject("p1")

        // Then
        assertThat(result).containsExactly(task1)
    }

    @Test
    fun `getTasksInProject should return empty list if no tasks matched`() = runTest {
        // Given
        coEvery {
            taskInProjectDataSource.getTasksInProjectByProjectId("p1")
        } returns listOf(TaskInProjectDto("99", "p1"))
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)

        // When
        val result = dataSource.getTasksInProject("p1")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getTasksByIds should return matching tasks`() = runTest {
        // Given
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)

        // When
        val result = dataSource.getTasksByIds(listOf("2"))

        // Then
        assertThat(result).containsExactly(task2)
    }

    @Test
    fun `getTasksByIds should return empty list when no ids match`() = runTest {
        // Given
        coEvery { taskReaderWriter.read() } returns listOf(task1)

        // When
        val result = dataSource.getTasksByIds(listOf("99"))

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `addTaskInProject should return true when add is successful`() = runTest {
        // Given
        coEvery {
            taskInProjectDataSource.addTaskInProject("p1", "1")
        } returns true

        // When
        val result = dataSource.addTaskInProject("p1", "1")

        // Then
        assertThat(result).isTrue()
        coVerify { taskInProjectDataSource.addTaskInProject("p1", "1") }
    }

    @Test
    fun `deleteTaskFromProject should return true when delete is successful`() = runTest {
        // Given
        coEvery {
            taskInProjectDataSource.deleteTaskFromProject("p1", "1")
        } returns true

        // When
        val result = dataSource.deleteTaskFromProject("p1", "1")

        // Then
        assertThat(result).isTrue()
        coVerify { taskInProjectDataSource.deleteTaskFromProject("p1", "1") }
    }

    @Test
    fun `getAllTasksByUserName should return tasks assigned to user`() = runTest {
        // Given
        coEvery {
            mateTaskAssignmentDataSource.getUsersMateTaskByUserName("Thoraya")
        } returns listOf(MateTaskAssignmentDto("Thoraya", "2"))
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)

        // When
        val result = dataSource.getAllTasksByUserName("Thoraya")

        // Then
        assertThat(result).containsExactly(task2)
    }

    @Test
    fun `getAllTasksByUserName should return empty list when user has no tasks`() = runTest {
        // Given
        coEvery {
            mateTaskAssignmentDataSource.getUsersMateTaskByUserName("empty_user")
        } returns listOf()
        coEvery { taskReaderWriter.read() } returns listOf(task1, task2)

        // When
        val result = dataSource.getAllTasksByUserName("empty_user")

        // Then
        assertThat(result).isEmpty()
    }
}