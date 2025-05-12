package org.example.logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

import org.example.logic.ReadDataException
import org.example.logic.TaskNotAddedException
import org.example.logic.TaskNotDeletedException
import org.example.logic.TasksNotFoundException
import org.example.logic.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import utils.buildTask
import java.util.*

class ManageTasksInProjectUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var useCase: ManageTasksInProjectUseCase

    private val projectId = UUID.randomUUID()
    private val taskId = UUID.randomUUID()
    private val testTask = buildTask(
        id = taskId,
        name = "Test Task",
        stateId = UUID.randomUUID(),
        projectName = "plan-mate",
    )

    @BeforeEach
    fun setup() {
        taskRepository = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        useCase = ManageTasksInProjectUseCase(getProjectsUseCase, taskRepository)
    }

    @Nested
    inner class GetTasksById {
        @Test
        fun `getTasksAssignedToProject() should return tasks when successful`() = runTest {
            // Given
            coEvery { taskRepository.getTasksInProject(projectId) } returns listOf(testTask)
            // When
            val result = useCase.getTasksInProjectById(projectId)
            // Then
            assertThat(result).containsExactly(testTask)
        }

        @Test
        fun `getTasksAssignedToProject() should return project repository failure`() = runTest {
            // Given
            coEvery { taskRepository.getTasksInProject(projectId) } throws ReadDataException()

            // When & Then
            assertThrows<ReadDataException> {
                useCase.getTasksInProjectById(projectId)
            }
        }

        @Test
        fun `getTasksAssignedToProject() should throw TasksNotFoundException when no tasks`() = runTest {
            coEvery { taskRepository.getTasksInProject(projectId) } returns emptyList()
            assertThrows<TasksNotFoundException> {
                useCase.getTasksInProjectById(projectId)
            }
        }
    }

    @Nested
    inner class AddTask {
        @Test
        fun `addTaskAssignedToProject() should return true when repository succeeds`() = runTest {
            // Given
            coEvery { taskRepository.addTaskInProject(projectId, taskId) } returns true
            // When
            val result = useCase.addTaskToProject(projectId, taskId)
            // Then
            assertThat(result).isTrue()
        }

        @Test
        fun `addTaskAssignedToProject() should return failure`() = runTest {
            // Given
            coEvery { taskRepository.addTaskInProject(projectId, taskId) } throws ReadDataException()

            // When & Then
            assertThrows<ReadDataException> {
                useCase.addTaskToProject(projectId, taskId)
            }
        }

        @Test
        fun `addTaskAssignedToProject() should throw TaskNotAddedException when repository returns false`() = runTest {
            coEvery { taskRepository.addTaskInProject(projectId, taskId) } returns false
            assertThrows<TaskNotAddedException> {
                useCase.addTaskToProject(projectId, taskId)
            }
        }
    }

    @Nested
    inner class DeleteTask {
        @Test
        fun `deleteTaskAssignedToProject() should return true when task exists and deletion succeeds`() = runTest {
            // Given
            coEvery { taskRepository.deleteTaskFromProject(projectId, taskId) } returns true

            // When
            val result = useCase.deleteTaskFromProject(projectId, taskId)

            // Then
            assertThat(result).isTrue()
        }

        @Test
        fun `deleteTaskAssignedToProject() should return delete failure`() = runTest {
            // Given
            coEvery { taskRepository.deleteTaskFromProject(projectId, taskId) } throws ReadDataException()

            // When & Then
            assertThrows<ReadDataException> {
                useCase.deleteTaskFromProject(projectId, taskId)
            }
        }

        @Test
        fun `deleteTaskAssignedToProject() should throw TaskNotDeletedException when repository returns false`() = runTest {
            coEvery { taskRepository.deleteTaskFromProject(projectId, taskId) } returns false
            assertThrows<TaskNotDeletedException> {
                useCase.deleteTaskFromProject(projectId, taskId)
            }
        }
    }

    @Nested
    inner class GetTasksByName {

        @Test
        fun `getTasksInProjectByName() should return tasks when project found and tasks exist`() = runTest {
            // Given
            val projectName = "Project"
            val project = buildProject(name = projectName)
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns project
            coEvery { taskRepository.getTasksInProject(project.id) } returns listOf(testTask)
            // When
            val result = useCase.getTasksInProjectByName(projectName)

            // Then
            assertThat(result).containsExactly(testTask)
            coVerify { taskRepository.getTasksInProject(project.id) }
        }

        @Test
        fun `getTasksInProjectByName() should throw TasksNotFoundException when no tasks`() = runTest {
            val projectName = "Project"
            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns buildProject(name = projectName)
            coEvery { taskRepository.getTasksInProject(testTask.id) } returns emptyList()

            assertThrows<TasksNotFoundException> {
                useCase.getTasksInProjectByName(projectName)
            }
        }

        @Test
        fun `getTasksInProjectByName() should return exception when getProjectByName fails`() = runTest {
            val projectName = "Proj"
            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws ReadDataException()

            assertThrows<ReadDataException> {
                useCase.getTasksInProjectByName(projectName)
            }
        }

    }

    @Nested
    inner class GetAllTasksByUserName {
        @Test
        fun `getAllTasksByUserName() should return tasks when tasks exist`() = runTest {
            val userName = "user1"
            coEvery { taskRepository.getAllTasksByUserName(userName) } returns listOf(testTask)

            val result = useCase.getAllTasksByUserName(userName)

            assertThat(result).containsExactly(testTask)
        }

        @Test
        fun `getAllTasksByUserName() should throw TasksNotFoundException when no tasks for user`() = runTest {
            val userName = "user2"
            coEvery { taskRepository.getAllTasksByUserName(userName) } returns emptyList()

            assertThrows<TasksNotFoundException> {
                useCase.getAllTasksByUserName(userName)
            }
        }

        @Test
        fun `getAllTasksByUserName() should return repository failure`() = runTest {
            val userName = "user3"
            coEvery { taskRepository.getAllTasksByUserName(userName) } throws ReadDataException()

            assertThrows<ReadDataException> {
                useCase.getAllTasksByUserName(userName)
            }
        }
    }
}
