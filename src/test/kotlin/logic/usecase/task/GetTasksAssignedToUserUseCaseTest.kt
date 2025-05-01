package logic.usecase.task

import io.mockk.every
import io.mockk.mockk
import org.example.data.entities.MateTaskAssignment
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTasksAssignedToUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetTasksAssignedToUserUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTasksAssignedToUserUseCase: GetTasksAssignedToUserUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getTasksAssignedToUserUseCase = GetTasksAssignedToUserUseCase(taskRepository)
    }

    @Test
    fun `getAllMateTaskAssignment() should return tasks when repository succeeds`() {
        // Given
        val userName = "Mate 1"
        val assignments = listOf(
            MateTaskAssignment(userName, "Task 1"),
            MateTaskAssignment(userName, "Task 2")
        )
        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(assignments)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertEquals(Result.success(assignments), result)
    }

    @Test
    fun `getAllMateTaskAssignment() should handle empty list gracefully`() {
        // Given
        val userName = "Mate 1"
        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(emptyList())

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertEquals(Result.success(emptyList<MateTaskAssignment>()), result)
    }

    @Test
    fun `getAllMateTaskAssignment() should throw exception when repository fails`() {
        // Given
        val userName = "Mate 1"
        val exception = RuntimeException("Database error")
        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.failure(exception)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertFailsWith<PlanMateExceptions.LogicException.NoTasksFound> {
            result.getOrThrow()
        }
    }

    @Test
    fun `getAllMateTaskAssignment() should handle invalid user name`() {
        // Given
        val userName = ""
        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertFailsWith<PlanMateExceptions.LogicException.NoTasksFound> {
            result.getOrThrow()
        }
    }
}