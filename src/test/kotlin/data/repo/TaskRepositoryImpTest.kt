package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import logic.model.entities.Task
import org.example.data.datasources.PlanMateDataSource
import org.example.data.entities.MateTaskAssignment
import org.example.data.repo.TaskRepositoryImp
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository
import org.example.utils.DateHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskRepositoryImpTest {

    private lateinit var taskDataSource: PlanMateDataSource<Task>
    private lateinit var mateTaskAssignmentCsvDataSource: PlanMateDataSource<MateTaskAssignment>
    private lateinit var taskRepository: TaskRepository
    private lateinit var dataHandler: DateHandler

    @BeforeEach
    fun setUp() {
        taskDataSource = mockk(relaxed = true)
        mateTaskAssignmentCsvDataSource = mockk(relaxed = true)
        taskRepository = TaskRepositoryImp(
            taskDataSource,
            mateTaskAssignmentCsvDataSource)
        dataHandler= DateHandler()
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        // Given
        val taskList = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate =dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime()),
            Task(id = "2", name = "Task 2", description = "Description 2", stateId = "state2", createdDate = dataHandler.getCurrentDateTime(), updatedDate =dataHandler.getCurrentDateTime())
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
        every { taskDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        // When
        val result = taskRepository.getAllTasks()

        // Then
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `createTask() should return success result with true when the task is created successfully`() {
        // Given
        val newTask = Task(
            id = "3",
            name = "Task 3",
            description = "Description 3",
            stateId = "state3",
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate =dataHandler.getCurrentDateTime(), updatedDate =dataHandler.getCurrentDateTime())
        )
        every { taskDataSource.read() } returns Result.success(existingTasks)
        every { taskDataSource.overWrite(any()) } returns Result.success(true)

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
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate =dataHandler.getCurrentDateTime(), updatedDate =dataHandler.getCurrentDateTime())
        )
        every { taskDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        // When
        val result = taskRepository.createTask(newTask)

        // Then
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `editTask() should return success result with true when the task is updated successfully`() {
        // Given
        val updatedTask = Task(
            id = "1",
            name = "Updated Task",
            description = "Updated Description",
            stateId = "state2",
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        )
        every { taskDataSource.read() } returns Result.success(existingTasks)
        every { taskDataSource.overWrite(any()) } returns Result.success(true)

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
            createdDate = dataHandler.getCurrentDateTime(),
            updatedDate = dataHandler.getCurrentDateTime()
        )
        every { taskDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        // When
        val result = taskRepository.editTask(updatedTask)

        // Then
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `deleteTask() should return success result with true when the task is deleted successfully`() {
        // Given
        val taskId = "1"
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
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
        val taskId = "999"
        val existingTasks = listOf(
            Task(id = "1", name = "Task 1", description = "Description 1", stateId = "state1", createdDate = dataHandler.getCurrentDateTime(), updatedDate = dataHandler.getCurrentDateTime())
        )
        every { taskDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        // When
        val result = taskRepository.deleteTask(taskId)

        // Then
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }
    @Test
    fun `getAllMateTaskAssignment should return success when read is successful`() {
        // Given
        val mateName = "Ali"
        val assignments = listOf(MateTaskAssignment("task1", "Ali"), MateTaskAssignment("task2", "Ali"))
        every {mateTaskAssignmentCsvDataSource.read() } returns Result.success(assignments)

        // When
        val result = taskRepository.getAllMateTaskAssignment(mateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(assignments)
    }
    @Test
    fun `getAllMateTaskAssignment should return failure with ReadException when read fails`() {
        // Given
        val mateName = "Ali"
        every { mateTaskAssignmentCsvDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        // When
        val result = taskRepository.getAllMateTaskAssignment(mateName)

        // Then
       assertThrows<PlanMateExceptions.DataException.ReadException> {
            result.getOrThrow()
        }
    }

}