package logic.usecase.project

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.ProjectValidationUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildProject
import java.util.*

class ProjectValidationUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var projectValidationUseCase: ProjectValidationUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        projectValidationUseCase = ProjectValidationUseCase(projectRepository)
    }

    @Test
    fun `isProjectNameExists returns true when project exists`() = runBlocking {
        val projectName = "TestProject"

        coEvery { projectRepository.getProjectByName(projectName) } returns buildProject(name = projectName)

        assertTrue(projectValidationUseCase.isProjectNameExists(projectName))
    }

    @Test
    fun `isProjectNameExists returns false when project does not exist`() = runBlocking {
        val projectName = "MissingProject"
        coEvery { projectRepository.getProjectByName(projectName) } throws Exception()
        assertFalse(projectValidationUseCase.isProjectNameExists(projectName))
    }

    @Test
    fun `isProjectExists returns true when project exists`() = runBlocking {
        val projectId = UUID.randomUUID()

        coEvery { projectRepository.getProjectById(projectId) } returns buildProject(id = projectId)

        assertTrue(projectValidationUseCase.isProjectExists(projectId))
    }

    @Test
    fun `isProjectExists returns false when project does not exist`() = runBlocking {
        val projectId = UUID.randomUUID()

        coEvery { projectRepository.getProjectById(projectId) } throws Exception()

        assertFalse(projectValidationUseCase.isProjectExists(projectId))
    }
}