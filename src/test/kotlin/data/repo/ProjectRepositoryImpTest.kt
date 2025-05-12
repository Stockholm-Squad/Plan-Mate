package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.data.repo.ProjectRepositoryImp
import org.example.data.source.ProjectDataSource
import org.example.logic.NoProjectAddedException
import org.example.logic.NoProjectDeletedException
import org.example.logic.NoProjectUpdatedException
import org.example.logic.NoProjectsFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import kotlin.test.Test

class ProjectRepositoryImpTest {
    private lateinit var projectDataSource: ProjectDataSource
    private lateinit var projectRepository: ProjectRepositoryImp


    @BeforeEach
    fun setUp() {
        projectDataSource = mockk()
        projectRepository = ProjectRepositoryImp(projectDataSource)
    }

    @Test
    fun `getProjectsByUsername() should returns list when successful`() = runTest {
        // Given
        val sampleProject = buildProject(name = "plan-mate").mapToProjectModel()
        coEvery { projectDataSource.getProjectsByUsername("user123") } returns listOf(sampleProject)
        //when
        val result = projectRepository.getProjectsByUsername("user123")
        //then
        assertThat(result).hasSize(1)
        assertThat(result.getOrNull(0)).isEqualTo(sampleProject.mapToProjectEntity())
    }

    @Test
    fun `getProjectsByUsername() should throws NoProjectsFoundException on failure when user is wrong`() = runTest {
        //Given
        coEvery { projectDataSource.getProjectsByUsername("user123") } throws NoProjectsFoundException()

        //When & Then
        assertThrows<NoProjectsFoundException> { projectRepository.getProjectsByUsername("user") }

    }

    @Test
    fun `getProjectsByUsername() should throws NoProjectsFoundException when failure`() = runTest {
        //Given
        coEvery { projectDataSource.getProjectsByUsername("user123") } throws NoProjectsFoundException()

        //When & Then
        assertThrows<NoProjectsFoundException> { projectRepository.getProjectsByUsername("user123") }

    }

    @Test
    fun `addProject() should returns true when successful`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery { projectDataSource.addProject(sampleProject.mapToProjectModel()) } returns true
        //When
        val result = projectRepository.addProject(sampleProject)
        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addProject() should throws NoProjectAddedException when failure`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery { projectDataSource.addProject(sampleProject.mapToProjectModel()) } throws NoProjectAddedException()
        //When & Then
        assertThrows<NoProjectAddedException> {
            projectRepository.addProject(sampleProject)
        }
    }

    @Test
    fun `updateProject() should returns true when successful`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery { projectDataSource.updateProject(sampleProject.mapToProjectModel()) } returns true
        //When
        val result = projectRepository.updateProject(sampleProject)
        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `updateProject() should throws NoProjectUpdatedException when failure`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery {
            projectDataSource.updateProject(sampleProject.mapToProjectModel())
        } throws NoProjectUpdatedException()
        //When & Then
        assertThrows<NoProjectUpdatedException> {
            projectRepository.updateProject(sampleProject)
        }
    }

    @Test
    fun `deleteProject() should returns true when successful`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery { projectDataSource.deleteProject(sampleProject.mapToProjectModel()) } returns true
        //When
        val result = projectRepository.deleteProject(sampleProject)
        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `deleteProject throws NoProjectDeletedException on failure`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery {
            projectDataSource.deleteProject(sampleProject.mapToProjectModel())
        } throws NoProjectDeletedException()

        //When & Then
        assertThrows<NoProjectDeletedException> { projectRepository.deleteProject(sampleProject) }
    }

    @Test
    fun `getAllProjects returns mapped list when successful`() = runTest {
        //Given
        val sampleProject = buildProject(name = "plan-mate")
        coEvery { projectDataSource.getAllProjects() } returns listOf(sampleProject.mapToProjectModel())
        //When
        val result = projectRepository.getAllProjects()
        //Then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(sampleProject)
    }

    @Test
    fun `getAllProjects throws NoProjectsFoundException on failure`() = runTest {
        //Given
        coEvery { projectDataSource.getAllProjects() } throws NoProjectsFoundException()
        //When & Then
        assertThrows<NoProjectsFoundException> { projectRepository.getAllProjects() }
    }

}