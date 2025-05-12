package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.NoProjectAddedException
import org.example.logic.NoProjectDeletedException
import org.example.logic.ProjectNotFoundException
import org.example.logic.entities.EntityState
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import java.util.*

class ManageProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        manageProjectUseCase = ManageProjectUseCase(projectRepository, manageStatesUseCase, getProjectsUseCase)
    }


    @Nested
    inner class AddProject {
        @Test
        fun `addProject() should return true when project is added successfully`() = runTest {
            // Given
            val project = buildProject(id = UUID.randomUUID(), name = "New Project")
            val state = EntityState(project.stateId, "todo")
            coEvery { projectRepository.addProject(project) } returns true

            // When
            val result = manageProjectUseCase.addProject(projectName = project.name, stateName = state.name)

            // Then
            coVerify { projectRepository.addProject(project) }
        }

        @Test
        fun `addProject() should throw when project addition fails`() = runTest {
            // Given
            val project = buildProject(id = UUID.randomUUID(), name = "New Project")
            val state = EntityState(project.stateId, "todo")
            coEvery { projectRepository.addProject(project) } throws NoProjectAddedException()

            // When & Then
            assertThrows<NoProjectAddedException> {
                manageProjectUseCase.addProject(project.name, state.name)
            }
            coVerify { projectRepository.addProject(project) }
        }
    }

    @Nested
    inner class UpdateProject {
        @Test
        fun `updateProject(() should return true when project exists and is updated`() = runTest {
            // Given
            val projectId = UUID.randomUUID()
            val existingProject = buildProject(id = projectId, name = "Existing Project")
            val updatedProject = existingProject.copy(name = "Updated Project")
            coEvery { projectRepository.getAllProjects() } returns listOf(existingProject)
            coEvery { projectRepository.editProject(updatedProject) } returns true

            // When
            val result = manageProjectUseCase.updateProject(
                projectId = projectId,
                newProjectName = updatedProject.name,
                newProjectStateName = "todo"
            )

            // Then
            assertThat(result).isTrue()

        }

        @Test
        fun `should return failure when project does not exist`() = runTest {
            // Given
            val projectId = UUID.randomUUID()
            val existingProject = buildProject(id = UUID.randomUUID(), name = "non existing")
            val updatedProject = existingProject.copy(name = "Updated Project")

            coEvery { projectRepository.editProject(existingProject) } throws ProjectNotFoundException()

            //  When & Then
            assertThrows<ProjectNotFoundException> {
                manageProjectUseCase.updateProject(
                    projectId = projectId,
                    newProjectName = updatedProject.name,
                    newProjectStateName = "todo"
                )
            }

        }


    }

    @Nested
    inner class RemoveProjectById {
        @Test
        fun `removeProjectByName() should return true when project exists and is deleted`() = runTest {
            // Given
            val existingProject = buildProject(id = /*projectId*/ UUID.randomUUID(), name = "Project to delete")
            coEvery { projectRepository.getAllProjects() } returns listOf(existingProject)
            coEvery { projectRepository.deleteProject(existingProject) } returns true

            // When
            val result = manageProjectUseCase.removeProjectByName(existingProject.name)

            // Then
            coVerify { projectRepository.getAllProjects() }
            coVerify { projectRepository.deleteProject(existingProject) }
        }

        @Test
        fun `removeProjectByName() should return empty list when project does not exist`() = runTest {
            // Given
            val projectName = "non-existent"
            coEvery { projectRepository.getAllProjects() } returns emptyList()

            // When
            val result = manageProjectUseCase.removeProjectByName(projectName)

            // Then
            coVerify { projectRepository.getAllProjects() }
            coVerify(exactly = 0) { projectRepository.deleteProject(any()) }
        }

        @Test
        fun `should return failure when repository fails to get projects`() = runTest {
            // Given
            val projectName = "123"
            coEvery { projectRepository.getAllProjects() } throws NoProjectDeletedException()

            // When & Then
            assertThrows<NoProjectDeletedException> {
                manageProjectUseCase.removeProjectByName(projectName)
            }
            coVerify { projectRepository.getAllProjects() }
            coVerify(exactly = 0) { projectRepository.deleteProject(any()) }
        }
    }

    @Nested
    inner class IsProjectExists {
        @Test
        fun `isProjectNameExists() should return true when project exists`() = runTest {
            // Given
            val projectId = "1"
            val existingProject = buildProject(id = UUID.randomUUID(), name = "Existing Project")
            coEvery { projectRepository.getAllProjects() } returns listOf(existingProject)

            // When
            val result = manageProjectUseCase.isProjectNameExists(projectId)

            // Then

            coVerify { projectRepository.getAllProjects() }
        }

        @Test
        fun `isProjectNameExists() should return empty list when project does not exist`() = runTest {
            // Given
            val projectId = "non-existent"
            coEvery { projectRepository.getAllProjects() } returns emptyList()

            // When
            val result = manageProjectUseCase.isProjectNameExists(projectId)

            // Then
            coVerify { projectRepository.getAllProjects() }
        }

        @Test
        fun `isProjectNameExists() should return failure when repository fails to get projects`() = runTest {
            // Given
            val projectId = "1"
            coEvery { projectRepository.getAllProjects() } throws ProjectNotFoundException()

            // When & Then
            assertThrows<Throwable> {
                manageProjectUseCase.isProjectNameExists(projectId)
            }
            coVerify { projectRepository.getAllProjects() }
        }
    }

}