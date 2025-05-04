package logic.usecase.project

import org.junit.jupiter.api.Assertions.*

import io.mockk.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import logic.model.entities.Task
import org.example.logic.repository.TaskRepository
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.model.exceptions.WriteDataException
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.Test


class ManageTasksInProjectUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var manageTasksInProjectUseCase: ManageTasksInProjectUseCase
    private lateinit var manageTaskUseCase: ManageTasksUseCase
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private val testTask = Task(
        id = UUID.randomUUID(),
        name = "Test Task",
        description = "",
        stateId = UUID.randomUUID(),
        createdDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
        updatedDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
    )
    private val anotherTask = Task(
        id = UUID.randomUUID(),
        name = "Another Task",
        description = "",
        stateId = UUID.randomUUID(),
        createdDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
        updatedDate = LocalDateTime(LocalDate(2005, 2,19), LocalTime(12,12)),
    )

    @BeforeEach
    fun setUp() {
        manageTaskUseCase = mockk()
        manageProjectUseCase = mockk()
        taskRepository = mockk()
        manageTasksInProjectUseCase = ManageTasksInProjectUseCase(manageTaskUseCase ,manageProjectUseCase ,taskRepository)
    }

    @Test
    fun `getTasksAssignedToProject should return tasks when successful`() {
        // Given
        val id = UUID.randomUUID()
        every { taskRepository.getTasksInProject(id) } returns Result.success(listOf(testTask, anotherTask))
        every { manageTaskUseCase.getTaskByName("101") } returns Result.success(testTask)
        every { manageTaskUseCase.getTaskByName("102") } returns Result.success(anotherTask)

        // When
        val result = manageTasksInProjectUseCase.getTasksInProjectById(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(testTask, anotherTask), result.getOrNull())
    }

    @Test
    fun `getTasksAssignedToProject should filter out null tasks`() {
        // Given
        val id = UUID.randomUUID()
        every { taskRepository.getTasksInProject(id) } returns Result.success(listOf(testTask, anotherTask, anotherTask))
        every { manageTaskUseCase.getTaskByName("101") } returns Result.success(testTask)
        every { manageTaskUseCase.getTaskByName("102") } returns Result.failure(RuntimeException())
        every { manageTaskUseCase.getTaskByName("103") } returns Result.success(anotherTask)

        // When
        val result = manageTasksInProjectUseCase.getTasksInProjectById(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(testTask, anotherTask), result.getOrNull())
    }

    @Test
    fun `getTasksAssignedToProject should propagate project repository failure`() {
        // Given
        val id = UUID.randomUUID()
        val expectedException = ReadDataException()
        every { taskRepository.getTasksInProject(id) } returns Result.failure(expectedException)

        // When
        val result = manageTasksInProjectUseCase.getTasksInProjectById(id)

        // Then
        assertTrue(result.isFailure)
        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `addTaskAssignedToProject should return success when repository succeeds`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        every { taskRepository.addTaskInProject(id1, id2) } returns Result.success(true)

        // When
        val result = manageTasksInProjectUseCase.addTaskToProject(id1, id2)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    @Test
    fun `addTaskAssignedToProject should propagate failure`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val expectedException = WriteDataException()
        every { taskRepository.addTaskInProject(id1, id2) } returns Result.failure(expectedException)

        // When
        val result = manageTasksInProjectUseCase.addTaskToProject(id1, id2)

        // Then
        assertTrue(result.isFailure)
        assertThrows<WriteDataException> { result.getOrThrow() }
    }

    @Test
    fun `deleteTaskAssignedToProject should return true when task exists and deletion succeeds`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        every { taskRepository.getTasksInProject(id1) } returns Result.success(listOf(testTask,anotherTask))
        every { taskRepository.deleteTaskFromProject(id1, id2) } returns Result.success(true)

        // When
        val result = manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    @Test
    fun `deleteTaskAssignedToProject should return false when task doesn't exist`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        every { taskRepository.getTasksInProject(id1) } returns Result.success(listOf(testTask))

        // When
        val result = manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow())
    }

    @Test
    fun `deleteTaskAssignedToProject should propagate read failure`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val expectedException = ReadDataException()
        every { taskRepository.getTasksInProject(id1) } returns Result.failure(expectedException)

        // When
        val result = manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        // Then
        assertTrue(result.isFailure)
        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `deleteTaskAssignedToProject should propagate delete failure`() {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        every { taskRepository.getTasksInProject(id1) } returns Result.success(listOf(testTask))
        val expectedException = WriteDataException()
        every { taskRepository.deleteTaskFromProject(id1, id2) } returns Result.failure(expectedException)

        // When
        val result = manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        // Then
        assertTrue(result.isFailure)
        assertThrows<WriteDataException> { result.getOrThrow() }
    }
}