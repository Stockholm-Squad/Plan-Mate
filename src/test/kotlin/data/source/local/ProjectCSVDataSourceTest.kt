package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.ProjectDto
import data.dto.UserAssignedToProjectDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.ProjectDataSource
import org.example.data.source.local.ProjectCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectCSVDataSourceTest {

    private lateinit var projectReaderWriter: IReaderWriter<ProjectDto>
    private lateinit var userAssignedToProjectReaderWriter: IReaderWriter<UserAssignedToProjectDto>
    private lateinit var dataSource: ProjectDataSource

    private val project1 = ProjectDto(id = "1", title = "Project A", stateId = "2")
    private val project2 = ProjectDto(id = "2", title = "Project B", stateId = "3")
    private val updatedProject = ProjectDto(id = "1", title = "Updated Project A", stateId = "4")

    @BeforeEach
    fun setup() {
        projectReaderWriter = mockk()
        userAssignedToProjectReaderWriter = mockk()
        dataSource = ProjectCSVDataSource(projectReaderWriter, userAssignedToProjectReaderWriter)
    }

    @Test
    fun `addProject should append to CSV and return true when project added`() = runTest {
        // Given
        coEvery { projectReaderWriter.append(listOf(project1)) } returns true

        // When
        val result = dataSource.addProject(project1)

        // Then
        assertThat(result).isTrue()
        coVerify { projectReaderWriter.append(listOf(project1)) }
    }

    @Test
    fun `addProject should return false when append fails`() = runTest {
        // Given
        coEvery { projectReaderWriter.append(listOf(project1)) } returns false

        // When
        val result = dataSource.addProject(project1)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `updateProject should overwrite with updated project when match found`() = runTest {
        // Given
        coEvery { projectReaderWriter.read() } returns listOf(project1, project2)
        coEvery { projectReaderWriter.overWrite(any()) } returns true

        // When
        val result = dataSource.updateProject(updatedProject)

        // Then
        assertThat(result).isTrue()
        coVerify {
            projectReaderWriter.overWrite(
                match { list ->
                    list.any { it.id == "1" && it.title == "Updated Project A" } &&
                            list.any { it.id == "2" && it.title == "Project B" }
                }
            )
        }
    }

    @Test
    fun `updateProject should return false when overwrite fails`() = runTest {
        // Given
        coEvery { projectReaderWriter.read() } returns listOf(project1)
        coEvery { projectReaderWriter.overWrite(any()) } returns false

        // When
        val result = dataSource.updateProject(updatedProject)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteProject should remove the matching project by id when delete called`() = runTest {
        // Given
        coEvery { projectReaderWriter.read() } returns listOf(project1, project2)
        coEvery { projectReaderWriter.overWrite(any()) } returns true

        // When
        val result = dataSource.deleteProject(project1)

        // Then
        assertThat(result).isTrue()
        coVerify {
            projectReaderWriter.overWrite(
                match { list -> list.size == 1 && list[0].id == "2" }
            )
        }
    }

    @Test
    fun `deleteProject should return false when overwrite fails`() = runTest {
        // Given
        coEvery { projectReaderWriter.read() } returns listOf(project1)
        coEvery { projectReaderWriter.overWrite(any()) } returns false

        // When
        val result = dataSource.deleteProject(project1)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getAllProjects should return list of projects when called`() = runTest {
        // Given
        coEvery { projectReaderWriter.read() } returns listOf(project1, project2)

        // When
        val result = dataSource.getAllProjects()

        // Then
        assertThat(result).containsExactly(project1, project2)
    }
}