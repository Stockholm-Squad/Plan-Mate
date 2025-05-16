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

    @Test
    fun `getAllProjects() should return list of project when successful`() = runTest {
        //Given
        val sampleProject1 = buildProject(name = "plan-mate")
        coEvery { projectRepository.getAllProjects() } returns listOf(sampleProject1)
        //When
        val result = getProjectsUseCase.getAllProjects()

        assertThat(result).hasSize(1)
    }

    @Test
    fun `getProjectByName() should return project when it exists`() = runTest {
        // Given
        val expectedProject = buildProject( name = "Test Project")
        coEvery { projectRepository.getProjectByName(expectedProject.title) } returns expectedProject

        // When
        val result = getProjectsUseCase.getProjectByName(expectedProject.title)

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `getProjectByName() should throw when project does not exist`() = runTest {
        // Given
        val projectName = "plan-mate"

        coEvery { projectRepository.getProjectByName(projectName) } throws ProjectNotFoundException()

        // When & Then
        assertThrows<ProjectNotFoundException> { getProjectsUseCase.getProjectByName(projectName) }
    }

    @Test
    fun `should return failure when repository fails`() = runTest {
        // Given
        val projectName = "plan-mate"

        coEvery { projectRepository.getProjectByName(projectName) } throws NoProjectsFoundException()


        // When & Then
        assertThrows<NoProjectsFoundException> {
            getProjectsUseCase.getProjectByName(projectName)
        }
    }
}

