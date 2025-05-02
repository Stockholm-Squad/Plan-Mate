package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.model.exceptions.NoObjectFound
import org.example.logic.model.exceptions.NoProjectAdded
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.ManageProjectUseCase
import org.junit.jupiter.api.*
import utils.buildProject
import kotlin.random.Random

class ManageProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var manageProjectUseCase: ManageProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        manageProjectUseCase = ManageProjectUseCase(projectRepository)
    }

    @Nested
    inner class GetAllProjects {
        @Test
        fun `should return all projects when repository succeeds`() {
            // Given
            val expectedProjects = listOf(
                buildProject(id = "1", name = "Project 1"),
                buildProject(id = "2", name = "Project 2")
            )
            every { projectRepository.getAllProjects() } returns Result.success(expectedProjects)

            // When
            val result = manageProjectUseCase.getAllProjects()

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(expectedProjects)

        }

        @Test
        fun `should return failure when repository fails`() {
            // Given
            val expectedException = NoObjectFound()
            every { projectRepository.getAllProjects() } returns Result.failure(expectedException)

            // When
            val result = manageProjectUseCase.getAllProjects()

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoObjectFound::class.java)
            verify { projectRepository.getAllProjects() }
        }
    }

    @Nested
    inner class GetProjectById {
        @Test
        fun `should return project when it exists`() {
            // Given
            val projectId = "3"
            val expectedProject = buildProject(id = projectId, name = "Test Project")
            val allProjects = listOf(
                buildProject(id = "1", name = "Project 1"),
                expectedProject,
                buildProject(id = "2", name = "Project 2")
            )
            every { projectRepository.getAllProjects() } returns Result.success(allProjects)

            // When
            val result = manageProjectUseCase.getProjectById(projectId)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(expectedProject)
            verify { projectRepository.getAllProjects() }
        }

        @Test
        fun `should return failure when project does not exist`() {
            // Given
            val projectId = "3"
            val allProjects = listOf(
                buildProject(id = "1", name = "Project 1"),
                buildProject(id = "2", name = "Project 2")
            )
            every { projectRepository.getAllProjects() } returns Result.success(allProjects)

            // When
            val result = manageProjectUseCase.getProjectById(projectId)

            // Then
            assertThrows<NoObjectFound> { result.getOrThrow() }
        }

        @Test
        fun `should return failure when repository fails`() {
            // Given
            val projectId = "123"

            every { projectRepository.getAllProjects() } returns Result.failure(NoProjectAdded())

            // When
            val result = manageProjectUseCase.getProjectById(projectId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoObjectFound::class.java)
            verify { projectRepository.getAllProjects() }
        }
    }

    @Nested
    inner class AddProject {
        @Test
        fun `should return success when project is added successfully`() {
            // Given
            val project = buildProject(id = Random.nextLong().toString(), name = "New Project")
            every { projectRepository.addProject(project) } returns Result.success(true)

            // When
            val result = manageProjectUseCase.addProject(project)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isTrue()
            verify { projectRepository.addProject(project) }
        }

        @Test
        fun `should return failure when project addition fails`() {
            // Given
            val project = buildProject(id = Random.nextLong().toString(), name = "New Project")
            val expectedException = Exception("Database error")
            every { projectRepository.addProject(project) } returns Result.failure(expectedException)

            // When
            val result = manageProjectUseCase.addProject(project)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoProjectAdded::class.java)
            verify { projectRepository.addProject(project) }
        }
    }

    @Nested
    inner class UpdateProject {
        @Test
        fun `should return success when project exists and is updated`() {
            // Given
            val projectId = "123"
            val existingProject = buildProject(id = projectId, name = "Existing Project")
            val updatedProject = existingProject.copy(name = "Updated Project")
            every { projectRepository.getAllProjects() } returns Result.success(listOf(existingProject))
            every { projectRepository.editProject(updatedProject) } returns Result.success(true)

            // When
            val result = manageProjectUseCase.updateProject(updatedProject)

            // Then
            assertThat(result.isSuccess).isTrue()
        }

        @Test
        fun `should return failure when project does not exist`() {
            // Given
            val projectId = "non-existent"
            val existingProject = buildProject(id = projectId, name = "Existing Project")
            every { projectRepository.editProject(existingProject) } returns Result.failure(NoObjectFound())

            // When
            val result = manageProjectUseCase.updateProject(existingProject)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoObjectFound::class.java)


        }


    }

    @Nested
    inner class RemoveProjectById {
        @Test
        fun `should return success when project exists and is deleted`() {
            // Given
            val projectId = "123"
            val existingProject = buildProject(id = projectId, name = "Project to delete")
            every { projectRepository.getAllProjects() } returns Result.success(listOf(existingProject))
            every { projectRepository.deleteProject(existingProject) } returns Result.success(true)

            // When
            val result = manageProjectUseCase.removeProjectById(projectId)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isTrue()
            verify { projectRepository.getAllProjects() }
            verify { projectRepository.deleteProject(existingProject) }
        }

        @Test
        fun `should return failure when project does not exist`() {
            // Given
            val projectId = "non-existent"
            every { projectRepository.getAllProjects() } returns Result.success(emptyList())

            // When
            val result = manageProjectUseCase.removeProjectById(projectId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoObjectFound::class.java)
            verify { projectRepository.getAllProjects() }
            verify(exactly = 0) { projectRepository.deleteProject(any()) }
        }

        @Test
        fun `should return failure when repository fails to get projects`() {
            // Given
            val projectId = "123"
            val expectedException = Exception("Database error")
            every { projectRepository.getAllProjects() } returns Result.failure(expectedException)

            // When
            val result = manageProjectUseCase.removeProjectById(projectId)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(NoObjectFound::class.java)
            verify { projectRepository.getAllProjects() }
            verify(exactly = 0) { projectRepository.deleteProject(any()) }
        }
    }

    @Nested
    inner class IsProjectExists {
        @Test
        fun `should return true when project exists`() {
            // Given
            val projectId = "1"
            val existingProject = buildProject(id = projectId, name = "Existing Project")
            every { projectRepository.getAllProjects() } returns Result.success(listOf(existingProject))

            // When
            val result = manageProjectUseCase.isProjectExists(projectId)

            // Then
            assertThat(result.getOrThrow()).isTrue()
            verify { projectRepository.getAllProjects() }
        }

        @Test
        fun `should return false when project does not exist`() {
            // Given
            val projectId = "non-existent"
            every { projectRepository.getAllProjects() } returns Result.success(emptyList())

            // When
            val result = manageProjectUseCase.isProjectExists(projectId)

            // Then
            assertThrows<Throwable> { result.getOrThrow() }
            verify { projectRepository.getAllProjects() }
        }

        @Test
        fun `should return failure when repository fails to get projects`() {
            // Given
            val projectId = "1"
            every { projectRepository.getAllProjects() } returns Result.failure(Exception("Database error"))

            // When
            val result = manageProjectUseCase.isProjectExists(projectId)

            // Then
            assertThrows<Throwable> { result.getOrThrow() }
            verify { projectRepository.getAllProjects() }
        }
    }

}