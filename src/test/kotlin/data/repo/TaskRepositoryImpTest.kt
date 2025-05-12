package data.repo

import com.google.common.truth.Truth.assertThat
import data.dto.MateTaskAssignmentDto
import data.dto.TaskInProjectDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.WriteDataException
import org.example.data.csv_reader_writer.mate_task_assignment.IMateTaskAssignmentCSVReaderWriter
import org.example.data.csv_reader_writer.task.TaskCSVReaderWriter
import org.example.data.csv_reader_writer.task_in_project.ITaskInProjectCSVReaderWriter
import org.example.data.repo.TaskRepositoryImp
import org.example.data.utils.DateHandlerImp
import org.example.logic.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildTask
import utils.buildTaskModel
import java.util.*


class TaskRepositoryImpTest {

    private lateinit var taskDataSource: TaskCSVReaderWriter
    private lateinit var mateTaskAssignmentCsvDataSource: IMateTaskAssignmentCSVReaderWriter
    private lateinit var taskInProjectDataSource: ITaskInProjectCSVReaderWriter
    private lateinit var taskRepository: TaskRepository
    private lateinit var dataHandler: DateHandlerImp
    private val projectUUID1 = UUID.randomUUID()
    private val projectUUID2 = UUID.randomUUID()
    private val taskUUID1 = UUID.randomUUID()
    private val taskUUID2 = UUID.randomUUID()
    private val testTaskInProject =
        TaskInProjectDto(projectId = projectUUID1.toString(), taskId = taskUUID1.toString())
    private val taskInProjectWithDifferentTaskId =
        TaskInProjectDto(projectId = projectUUID1.toString(), taskId = taskUUID2.toString())
    private val taskInProjectWithDifferentProjectId =
        TaskInProjectDto(projectId = projectUUID2.toString(), taskId = taskUUID1.toString())


    @BeforeEach
    fun setUp() {
        taskDataSource = mockk(relaxed = true)
        mateTaskAssignmentCsvDataSource = mockk(relaxed = true)
        taskRepository = TaskRepositoryImp(
            taskDataSource,
            mateTaskAssignmentCsvDataSource,
            taskInProjectDataSource
        )
        dataHandler = DateHandlerImp()
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        // Given
        val taskList = listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State"),
            buildTaskModel(name = "Task 2", description = "Description 2", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.success(taskList)

        // When
        val result = taskRepository.getAllTasks()

        // Then
        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getAllTasks() should return failure result with throwable when error happens while reading from the file`() {
        // Given
        every { taskDataSource.read() } returns Result.failure(ReadDataException())

        // When
        val result = taskRepository.getAllTasks()

        // Then
        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `createTask() should return success result with true when the task is created successfully`() {
        // Given
        val newTask = buildTask(name = "Task 3", description = "Description 3", stateId = UUID.randomUUID())
        val existingTasks = listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.success(existingTasks)
        every { taskDataSource.overWrite(any()) } returns Result.success(true)

        // When
        val result = taskRepository.addTask(newTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `createTask() should return failure result with throwable when the task already exists`() {
        // Given
        val newTask = buildTask(name = "Task 1", description = "Description 1", stateId = UUID.randomUUID())
        val existingTasks = listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.success(existingTasks)

        // When
        val result = taskRepository.addTask(newTask)

        // Then
        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `updateTask() should return success result with true when the task is updated successfully`() {
        // Given
        val updatedTask = buildTask(name = "Updated Task", description = "Updated Description", stateId = UUID.randomUUID())
        val existingTasks = listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.success(existingTasks)
        every { taskDataSource.overWrite(any()) } returns Result.success(true)

        // When
        val result = taskRepository.updateTask(updatedTask)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `updateTask() should return failure result with throwable when the task to update does not exist`() {
        // Given
        val updatedTask = buildTask(name = "Updated Task", description = "Updated Description", stateId = UUID.randomUUID())
        listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.failure(ReadDataException())

        // When
        val result = taskRepository.updateTask(updatedTask)

        // Then
        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `deleteTask() should return success result with true when the task is deleted successfully`() {
        // Given
        val taskId = UUID.randomUUID()
        val existingTasks = listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.success(existingTasks)
        every { taskDataSource.overWrite(any()) } returns Result.success(true)

        // When
        val result = taskRepository.deleteTask(taskId)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteTask() should return failure result with throwable when the task to delete does not exist`() {
        // Given
        val taskId = UUID.randomUUID()
        listOf(
            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
        )
        every { taskDataSource.read() } returns Result.failure(ReadDataException())

        // When
        val result = taskRepository.deleteTask(taskId)

        // Then
        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `getAllMateTaskAssignment should return success when read is successful`() {
        // Given
        val mateName = "Ali"
        val assignments = listOf(MateTaskAssignmentDto("task1", "Ali"), MateTaskAssignmentDto("task2", "Ali"))
        every { mateTaskAssignmentCsvDataSource.read() } returns Result.success(assignments)

        // When
        val result = taskRepository.getAllTasksByUserName(mateName)

        // Then
        assertThat(result.getOrNull()).isEqualTo(assignments)
    }

    @Test
    fun `getAllMateTaskAssignment should return failure with ReadException when read fails`() {
        // Given
        val mateName = "Ali"
        every { mateTaskAssignmentCsvDataSource.read() } returns Result.failure(ReadDataException())

        // When
        val result = taskRepository.getAllTasksByUserName(mateName)

        // Then
        assertThrows<ReadDataException> { result.getOrThrow() }
    }


    @Nested
    inner class TasksInProjectTests {

        @Test
        fun `getTasksInProject should return list of task IDs for given project`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
            )

            val result = taskRepository.getTasksInProject(projectUUID1)

            assertThat(result.getOrNull()).containsExactly("101", "102")
        }

        @Test
        fun `getTasksInProject should return empty list when no tasks for project`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(TaskInProjectDto(projectId = "2", taskId = "201"))
            )

            val result = taskRepository.getTasksInProject(projectUUID1)

            assertThat(result.getOrNull()).isEmpty()
        }

        @Test
        fun `getTasksInProject should return failure when read fails`() {
            every { taskInProjectDataSource.read() } returns Result.failure(
                ReadDataException()
            )

            val result = taskRepository.getTasksInProject(projectUUID1)

            assertThrows<ReadDataException> { result.getOrThrow() }
        }

        @Test
        fun `addTaskInProject should append task and return success`() {
            every { taskInProjectDataSource.append(any()) } returns Result.success(true)

            val result = taskRepository.addTaskInProject(projectUUID1, taskUUID1)

            assertThat(result.getOrThrow()).isTrue()
            verify { taskInProjectDataSource.append(listOf(TaskInProjectDto(projectId = "1", taskId = "101"))) }
        }

        @Test
        fun `addTaskInProject should return failure when write fails`() {
            every { taskInProjectDataSource.append(any()) } returns Result.failure(
                WriteDataException()
            )

            val result = taskRepository.addTaskInProject(projectUUID1, taskUUID1)

            assertThrows<WriteDataException> { result.getOrThrow() }
        }

        @Test
        fun `deleteTaskFromProject should remove task and return success`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = taskRepository.deleteTaskFromProject(projectUUID1, taskUUID1)

            assertThat(result.isSuccess).isTrue()
            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentTaskId)) }
        }

        @Test
        fun `deleteTaskFromProject should return success when task not found`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(taskInProjectWithDifferentTaskId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = taskRepository.deleteTaskFromProject(projectUUID1, taskUUID1)

            assertThat(result.isSuccess).isTrue()
            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentTaskId)) }
        }

        @Test
        fun `deleteTaskFromProject should return success when project not found`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(taskInProjectWithDifferentProjectId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = taskRepository.deleteTaskFromProject(projectUUID1, taskUUID1)

            assertThat(result.isSuccess).isTrue()
            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentProjectId)) }
        }

        @Test
        fun `deleteTaskFromProject should return failure when read fails`() {
            every { taskInProjectDataSource.read() } returns Result.failure(
                ReadDataException()
            )

            val result = taskRepository.deleteTaskFromProject(projectUUID1, taskUUID1)

            assertThrows<ReadDataException> { result.getOrThrow() }
        }

        @Test
        fun `deleteTaskFromProject should return failure when write fails`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.failure(
                WriteDataException()
            )

            val result = taskRepository.deleteTaskFromProject(projectUUID1, taskUUID1)

            assertThrows <WriteDataException> { result.getOrThrow() }
        }
    }

}
