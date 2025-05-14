package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.MateTaskAssignmentDto
import data.dto.TaskDto
import data.dto.TaskInProjectDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.TaskDataSource
import org.example.data.source.local.TaskCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.example.data.source.local.csv_reader_writer.MateTaskAssignmentCSVReaderWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildTaskModel

class TaskCSVDataSourceTest {

    private lateinit var taskReaderWriter: IReaderWriter<TaskDto>
    private lateinit var mateTaskAssignmentReaderWriter: MateTaskAssignmentCSVReaderWriter
    private lateinit var taskInProjectReaderWriter: IReaderWriter<TaskInProjectDto>
    private lateinit var dataSource: TaskDataSource

    private val task1 = buildTaskModel(id = "1", title = "Design")
    private val task2 = buildTaskModel(id = "2", title = "Development")
    private val updatedTask = buildTaskModel(id = "1", title = "UI Design")

    private val dto1 = MateTaskAssignmentDto(username = "Thoraya", taskId = "task1")
    private val dto2 = MateTaskAssignmentDto(username = "Hanan", taskId = "task2")
    private val dto3 = MateTaskAssignmentDto(username = "Thoraya", taskId = "task3")


    @BeforeEach
    fun setup() {
        taskReaderWriter = mockk(relaxed = true)
        mateTaskAssignmentReaderWriter = mockk(relaxed = true)
        taskInProjectReaderWriter = mockk(relaxed = true)
        dataSource = TaskCSVDataSource(taskReaderWriter, mateTaskAssignmentReaderWriter, taskInProjectReaderWriter)
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
}