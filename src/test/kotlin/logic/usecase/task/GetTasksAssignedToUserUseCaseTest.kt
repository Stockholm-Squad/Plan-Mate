package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import data.models.MateTaskAssignment
import org.example.logic.model.exceptions.NoTaskAssignmentFound
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTasksAssignedToUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildMateTaskAssignment


class GetTasksAssignedToUserUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTasksAssignedToUserUseCase: GetTasksAssignedToUserUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getTasksAssignedToUserUseCase = GetTasksAssignedToUserUseCase(taskRepository)
    }

    @Test
    fun `getAllMateTaskAssignment should return success when repository returns valid list`() {
        // Given
        val userName = "Alice"
        val assignments = listOf(
            buildMateTaskAssignment(userName = "Alice", taskId = "1"),
            buildMateTaskAssignment(userName = "Alice", taskId = "2")
        )

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(assignments)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(assignments)
    }

    @Test
    fun `getAllMateTaskAssignment should return empty list when repository returns empty`() {
        // Given
        val userName = "Bob"
        val emptyAssignments = emptyList<MateTaskAssignment>()

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(emptyAssignments)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(emptyAssignments)
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getAllMateTaskAssignment should return failure when repository fails with generic error`() {
        // Given
        val userName = "Charlie"
        val error = Exception("Database connection failed")

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.failure(error)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThrows<NoTaskAssignmentFound> {
            result.getOrThrow()
        }
    }

    @Test
    fun `getAllMateTaskAssignment should return failure when repository returns NoTaskAssignmentFound`() {
        // Given
        val userName = "David"
        val error = NoTaskAssignmentFound()

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.failure(error)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThrows<NoTaskAssignmentFound> {
            result.getOrThrow()
        }
    }

    @Test
    fun `getAllMateTaskAssignment should return failure when repository returns unexpected exception`() {
        // Given
        val userName = "Eve"
        val error = RuntimeException("Unknown error occurred")

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.failure(error)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isFailure).isTrue()

        assertThrows<NoTaskAssignmentFound> {
            result.getOrThrow()
        }
    }

    @Test
    fun `getAllMateTaskAssignment should propagate successful empty list correctly`() {
        // Given
        val userName = "Frank"
        val emptyList = emptyList<MateTaskAssignment>()

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(emptyList)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(emptyList)
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getAllMateTaskAssignment should not transform success into failure for blank list`() {
        // Given
        val userName = "Grace"
        val emptyList = emptyList<MateTaskAssignment>()

        every { taskRepository.getAllMateTaskAssignment(userName) } returns Result.success(emptyList)

        // When
        val result = getTasksAssignedToUserUseCase.getAllMateTaskAssignment(userName)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(emptyList)
        assertThat(result.getOrNull()).isEmpty()
    }
}