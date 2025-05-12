package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.NoProjectsFoundException
import org.example.logic.ProjectNotFoundException
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import java.util.*

class GetProjectsUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var getProjectsUseCase: GetProjectsUseCase


    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getProjectsUseCase = GetProjectsUseCase(projectRepository)
    }

    @Nested
    inner class GetProjectByName {
        @Test
        fun `getProjectByName() should return project when it exists`() = runTest {
            // Given
            val projectId = UUID.randomUUID()
            val expectedProject = buildProject(id = projectId, name = "Test Project")
            val allProjects = listOf(
                buildProject(id = UUID.randomUUID(), name = "Project 1"),
                expectedProject,
                buildProject(id = UUID.randomUUID(), name = "Project 2")
            )
            coEvery { projectRepository.getAllProjects() } returns allProjects

            // When
            val result = getProjectsUseCase.getProjectByName(expectedProject.name)

            // Then
            assertThat { result }.isEqualTo(expectedProject)
            coVerify { projectRepository.getAllProjects() }
        }

        @Test
        fun `getProjectByName() should throw when project does not exist`() = runTest {
            // Given
            UUID.randomUUID()
            val allProjects = listOf(
                buildProject(id = UUID.randomUUID(), name = "Project 1"),
                buildProject(id = UUID.randomUUID(), name = "Project 2")
            )
            coEvery { projectRepository.getAllProjects() } returns allProjects

            // When & Then
            assertThrows<ProjectNotFoundException> { getProjectsUseCase.getProjectByName("not exist project") }
        }

        @Test
        fun `should return failure when repository fails`() = runTest {
            // Given
            val projectId = "123"

            coEvery { projectRepository.getAllProjects() } throws NoProjectsFoundException()


            // When & Then
            assertThrows<NoProjectsFoundException> {
                getProjectsUseCase.getProjectByName(projectId)
            }
            coVerify { projectRepository.getAllProjects() }
        }
    }

}