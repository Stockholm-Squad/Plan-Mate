//package data.repo
//
//import com.google.common.truth.Truth.assertThat
//import io.mockk.every
//import io.mockk.mockk
//import data.models.MateTaskAssignment
//import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
//import org.example.data.datasources.task_In_project_data_source.ITaskInProjectDataSource
//import org.example.data.datasources.task_data_source.TaskCsvDataSource
//import org.example.data.repo.TaskRepositoryImp
//import org.example.logic.model.exceptions.ReadDataException
//import org.example.logic.repository.TaskRepository
//import org.example.data.utils.DateHandlerImp
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import utils.buildTask
//import utils.buildTaskModel
//import java.util.UUID
//
//class TaskRepositoryImpTest {
//
//    private lateinit var taskDataSource: TaskCsvDataSource
//    private lateinit var mateTaskAssignmentCsvDataSource: IMateTaskAssignmentDataSource
//    private lateinit var taskInProjectDataSource: ITaskInProjectDataSource
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var dataHandler: DateHandlerImp
//
//    @BeforeEach
//    fun setUp() {
//        taskDataSource = mockk(relaxed = true)
//        mateTaskAssignmentCsvDataSource = mockk(relaxed = true)
//        taskRepository = TaskRepositoryImp(
//            taskDataSource,
//            mateTaskAssignmentCsvDataSource,
//            taskInProjectDataSource
//        )
//        dataHandler = DateHandlerImp()
//    }
//
//    @Test
//    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
//        // Given
//        val taskList = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State"),
//            buildTaskModel(name = "Task 2", description = "Description 2", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.success(taskList)
//
//        // When
//        val result = taskRepository.getAllTasks()
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(taskList)
//    }
//
//    @Test
//    fun `getAllTasks() should return failure result with throwable when error happens while reading from the file`() {
//        // Given
//        every { taskDataSource.read() } returns Result.failure(ReadDataException())
//
//        // When
//        val result = taskRepository.getAllTasks()
//
//        // Then
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `createTask() should return success result with true when the task is created successfully`() {
//        // Given
//        val newTask = buildTask(name = "Task 3", description = "Description 3", stateId = UUID.randomUUID())
//        val existingTasks = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.success(existingTasks)
//        every { taskDataSource.overWrite(any()) } returns Result.success(true)
//
//        // When
//        val result = taskRepository.addTask(newTask)
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(true)
//    }
//
//    @Test
//    fun `createTask() should return failure result with throwable when the task already exists`() {
//        // Given
//        val newTask = buildTask(name = "Task 1", description = "Description 1", stateId = UUID.randomUUID())
//        val existingTasks = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.success(existingTasks)
//
//        // When
//        val result = taskRepository.addTask(newTask)
//
//        // Then
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `editTask() should return success result with true when the task is updated successfully`() {
//        // Given
//        val updatedTask = buildTask(name = "Updated Task", description = "Updated Description", stateId = UUID.randomUUID())
//        val existingTasks = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.success(existingTasks)
//        every { taskDataSource.overWrite(any()) } returns Result.success(true)
//
//        // When
//        val result = taskRepository.editTask(updatedTask)
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(true)
//    }
//
//    @Test
//    fun `editTask() should return failure result with throwable when the task to edit does not exist`() {
//        // Given
//        val updatedTask = buildTask(name = "Updated Task", description = "Updated Description", stateId = UUID.randomUUID())
//        val existingTasks = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.failure(ReadDataException())
//
//        // When
//        val result = taskRepository.editTask(updatedTask)
//
//        // Then
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `deleteTask() should return success result with true when the task is deleted successfully`() {
//        // Given
//        val taskId = UUID.randomUUID()
//        val existingTasks = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.success(existingTasks)
//        every { taskDataSource.overWrite(any()) } returns Result.success(true)
//
//        // When
//        val result = taskRepository.deleteTask(taskId)
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(true)
//    }
//
//    @Test
//    fun `deleteTask() should return failure result with throwable when the task to delete does not exist`() {
//        // Given
//        val taskId = UUID.randomUUID()
//        val existingTasks = listOf(
//            buildTaskModel(name = "Task 1", description = "Description 1", stateId = "State")
//        )
//        every { taskDataSource.read() } returns Result.failure(ReadDataException())
//
//        // When
//        val result = taskRepository.deleteTask(taskId)
//
//        // Then
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `getAllMateTaskAssignment should return success when read is successful`() {
//        // Given
//        val mateName = "Ali"
//        val assignments = listOf(MateTaskAssignment("task1", "Ali"), MateTaskAssignment("task2", "Ali"))
//        every { mateTaskAssignmentCsvDataSource.read() } returns Result.success(assignments)
//
//        // When
//        val result = taskRepository.getAllTasksByUserName(mateName)
//
//        // Then
//        assertThat(result.getOrNull()).isEqualTo(assignments)
//    }
//
//    @Test
//    fun `getAllMateTaskAssignment should return failure with ReadException when read fails`() {
//        // Given
//        val mateName = "Ali"
//        every { mateTaskAssignmentCsvDataSource.read() } returns Result.failure(ReadDataException())
//
//        // When
//        val result = taskRepository.getAllTasksByUserName(mateName)
//
//        // Then
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//}
