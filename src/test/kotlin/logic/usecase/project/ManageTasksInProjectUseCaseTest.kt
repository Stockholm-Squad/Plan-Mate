package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.example.logic.ReadDataException
import org.example.logic.entities.Task
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test


class ManageTasksInProjectUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageTasksInProjectUseCase: ManageTasksInProjectUseCase
    private val testTask = Task(
        id = UUID.randomUUID(),
        name = "Test Task",
        description = "",
        stateId = UUID.randomUUID(),
        createdDate = LocalDateTime(LocalDate(2005, 2, 19), LocalTime(12, 12)),
        updatedDate = LocalDateTime(LocalDate(2005, 2, 19), LocalTime(12, 12)),
        projectName = "Project",
    )
    private val anotherTask = Task(
        id = UUID.randomUUID(),
        name = "Another Task",
        description = "",
        stateId = UUID.randomUUID(),
        createdDate = LocalDateTime(LocalDate(2005, 2, 19), LocalTime(12, 12)),
        updatedDate = LocalDateTime(LocalDate(2005, 2, 19), LocalTime(12, 12)),
        projectName = "Another Project",
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        getProjectsUseCase = mockk()
        manageTasksInProjectUseCase = ManageTasksInProjectUseCase(getProjectsUseCase, taskRepository)
    }

    @Test
    fun `getTasksAssignedToProject() should return tasks when successful`() = runTest {
        // Given
        val id = UUID.randomUUID()
        coEvery { taskRepository.getTasksInProject(id) } returns listOf(testTask, anotherTask)

        // When
        val result = manageTasksInProjectUseCase.getTasksInProjectById(id)

        // Then
        assertThat { result }.isEqualTo(listOf(testTask, anotherTask))
    }


    @Test
    fun `getTasksAssignedToProject() should propagate project repository failure`() = runTest {
        // Given
        val id = UUID.randomUUID()
        coEvery { taskRepository.getTasksInProject(id) } throws ReadDataException()

        //  When & Then
        assertThrows<ReadDataException> {
            manageTasksInProjectUseCase.getTasksInProjectById(id)
        }
    }

    @Test
    fun `addTaskAssignedToProject() should return true when repository succeeds`() = runTest {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        coEvery { taskRepository.addTaskInProject(id1, id2) } returns true

        // When
        val result = manageTasksInProjectUseCase.addTaskToProject(id1, id2)

        // Then
        assertThat { result }.isEqualTo(true)
    }

    @Test
    fun `addTaskAssignedToProject() should propagate failure`() = runTest {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        coEvery { taskRepository.addTaskInProject(id1, id2) } throws Throwable()

        // When & Then
        assertThrows<Throwable> {
            manageTasksInProjectUseCase.addTaskToProject(id1, id2)
        }
    }

    @Test
    fun `deleteTaskAssignedToProject() should return true when task exists and deletion succeeds`() = runTest {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        coEvery { taskRepository.getTasksInProject(id1) } returns listOf(testTask, anotherTask)
        coEvery { taskRepository.deleteTaskFromProject(id1, id2) } returns true

        // When
        val result = manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        // Then
        assertThat { result }.isEqualTo(true)

    }

    @Test
    fun `deleteTaskAssignedToProject() should return false when task doesn't exist`() = runTest {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        coEvery { taskRepository.getTasksInProject(id1) } returns listOf(testTask)

        // When
        val result = manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        // Then
        assertThat { result }.isEqualTo(listOf(testTask))
    }

    @Test
    fun `deleteTaskAssignedToProject() should propagate read failure`() = runTest {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        coEvery { taskRepository.getTasksInProject(id1) } throws ReadDataException()

        //  When & Then
        assertThrows<ReadDataException> {
            manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)
        }
    }

    @Test
    fun `deleteTaskAssignedToProject should propagate delete failure`() = runTest {
        // Given
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        coEvery { taskRepository.getTasksInProject(id1) } returns listOf(testTask)
        coEvery { taskRepository.deleteTaskFromProject(id1, id2) } throws Throwable()


        // When & Then
        assertThrows<Throwable> {
            manageTasksInProjectUseCase.deleteTaskFromProject(id1, id2)

        }
    }
}