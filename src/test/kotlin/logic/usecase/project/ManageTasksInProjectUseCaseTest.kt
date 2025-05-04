package logic.usecase.project

//import org.junit.jupiter.api.Assertions.*
//
//import io.mockk.*
//import kotlinx.datetime.LocalDate
//import kotlinx.datetime.LocalDateTime
//import kotlinx.datetime.LocalTime
//import logic.model.entities.Task
//import org.example.logic.repository.ProjectRepository
//import org.example.logic.repository.TaskRepository
//import org.example.logic.model.exceptions.ReadDataException
//import org.example.logic.model.exceptions.WriteDataException
//import org.example.logic.usecase.project.ManageTasksInProjectUseCase
//import org.example.logic.usecase.task.ManageTasksUseCase
//import org.junit.jupiter.api.*
//import kotlin.test.Test
//
//
//class ManageTasksInProjectUseCaseTest {
//    private lateinit var projectRepository: ProjectRepository
//    private lateinit var taskRepository: TaskRepository
//    private lateinit var useCase: ManageTasksInProjectUseCase
//    private lateinit var manageTaskUseCase: ManageTasksUseCase
//    private val testTask = Task(
//        id = "101", name = "Test Task",
//        description = "",
//        stateId = "",
//        createdDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
//        updatedDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
//    )
//    private val anotherTask = Task(
//        id = "102", name = "Another Task",
//        description = "",
//        stateId = "",
//        createdDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
//        updatedDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
//    )
//
//    @BeforeEach
//    fun setUp() {
//        projectRepository = mockk()
//        taskRepository = mockk()
//        manageTaskUseCase = mockk()
//        useCase = ManageTasksInProjectUseCase(projectRepository, manageTaskUseCase)
//    }
//
//    @Test
//    fun `getTasksAssignedToProject should return tasks when successful`() {
//        // Given
//        every { projectRepository.getTasksInProject("1") } returns Result.success(listOf("101", "102"))
//        every { manageTaskUseCase.getTaskById("101") } returns Result.success(testTask)
//        every { manageTaskUseCase.getTaskById("102") } returns Result.success(anotherTask)
//
//        // When
//        val result = useCase.getTasksAssignedToProject("1")
//
//        // Then
//        assertTrue(result.isSuccess)
//        assertEquals(listOf(testTask, anotherTask), result.getOrNull())
//    }
//
//    @Test
//    fun `getTasksAssignedToProject should filter out null tasks`() {
//        // Given
//        every { projectRepository.getTasksInProject("1") } returns Result.success(listOf("101", "102", "103"))
//        every { manageTaskUseCase.getTaskById("101") } returns Result.success(testTask)
//        every { manageTaskUseCase.getTaskById("102") } returns Result.failure(RuntimeException())
//        every { manageTaskUseCase.getTaskById("103") } returns Result.success(anotherTask)
//
//        // When
//        val result = useCase.getTasksAssignedToProject("1")
//
//        // Then
//        assertTrue(result.isSuccess)
//        assertEquals(listOf(testTask, anotherTask), result.getOrNull())
//    }
//
//    @Test
//    fun `getTasksAssignedToProject should propagate project repository failure`() {
//        // Given
//        val expectedException = ReadDataException()
//        every { projectRepository.getTasksInProject("1") } returns Result.failure(expectedException)
//
//        // When
//        val result = useCase.getTasksAssignedToProject("1")
//
//        // Then
//        assertTrue(result.isFailure)
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `addTaskAssignedToProject should return success when repository succeeds`() {
//        // Given
//        every { projectRepository.addTaskInProject("1", "101") } returns Result.success(true)
//
//        // When
//        val result = useCase.addTaskToProject("1", "101")
//
//        // Then
//        assertTrue(result.isSuccess)
//        assertTrue(result.getOrThrow())
//    }
//
//    @Test
//    fun `addTaskAssignedToProject should propagate failure`() {
//        // Given
//        val expectedException = WriteDataException()
//        every { projectRepository.addTaskInProject("1", "101") } returns Result.failure(expectedException)
//
//        // When
//        val result = useCase.addTaskToProject("1", "101")
//
//        // Then
//        assertTrue(result.isFailure)
//        assertThrows<WriteDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `deleteTaskAssignedToProject should return true when task exists and deletion succeeds`() {
//        // Given
//        every { projectRepository.getTasksInProject("1") } returns Result.success(listOf("101", "102"))
//        every { projectRepository.deleteTaskFromProject("1", "101") } returns Result.success(true)
//
//        // When
//        val result = useCase.deleteTaskFromProject("1", "101")
//
//        // Then
//        assertTrue(result.isSuccess)
//        assertTrue(result.getOrThrow())
//    }
//
//    @Test
//    fun `deleteTaskAssignedToProject should return false when task doesn't exist`() {
//        // Given
//        every { projectRepository.getTasksInProject("1") } returns Result.success(listOf("102"))
//
//        // When
//        val result = useCase.deleteTaskFromProject("1", "101")
//
//        // Then
//        assertTrue(result.isSuccess)
//        assertFalse(result.getOrThrow())
//    }
//
//    @Test
//    fun `deleteTaskAssignedToProject should propagate read failure`() {
//        // Given
//        val expectedException = ReadDataException()
//        every { projectRepository.getTasksInProject("1") } returns Result.failure(expectedException)
//
//        // When
//        val result = useCase.deleteTaskFromProject("1", "101")
//
//        // Then
//        assertTrue(result.isFailure)
//        assertThrows<ReadDataException> { result.getOrThrow() }
//    }
//
//    @Test
//    fun `deleteTaskAssignedToProject should propagate delete failure`() {
//        // Given
//        every { projectRepository.getTasksInProject("1") } returns Result.success(listOf("101"))
//        val expectedException = WriteDataException()
//        every { projectRepository.deleteTaskFromProject("1", "101") } returns Result.failure(expectedException)
//
//        // When
//        val result = useCase.deleteTaskFromProject("1", "101")
//
//        // Then
//        assertTrue(result.isFailure)
//        assertThrows<WriteDataException> { result.getOrThrow() }
//    }
//}