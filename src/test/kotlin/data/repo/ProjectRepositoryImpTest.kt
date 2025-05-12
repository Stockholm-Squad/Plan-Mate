package data.repo

import com.google.common.truth.Truth.assertThat
import data.dto.ProjectDto
import io.mockk.every
import io.mockk.mockk
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.WriteDataException
import org.example.data.csv_reader_writer.project.IProjectCSVReaderWriter
import org.example.data.csv_reader_writer.task_in_project.TaskInProjectCSVReaderWriter
import org.example.data.csv_reader_writer.user_assigned_to_project.IUserAssignedToProjectCSVReaderWriter
import org.example.data.repo.ProjectRepositoryImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import kotlin.test.Test

class ProjectRepositoryImpTest {

    private lateinit var projectDataSource: IProjectCSVReaderWriter
    private lateinit var taskInProjectDataSource: TaskInProjectCSVReaderWriter
    private lateinit var userAssignedToProjectDataSource: IUserAssignedToProjectCSVReaderWriter
    private lateinit var projectRepositoryImp: ProjectRepositoryImp
    private val testProject = buildProject(name = "Test Project")
    private val anotherProject = buildProject(name = "Another Project")
    private val projectDto = ProjectDto("1", "name", "12")

    @BeforeEach
    fun setUp() {
        projectDataSource = mockk(relaxed = true)
        taskInProjectDataSource = mockk(relaxed = true)
        userAssignedToProjectDataSource = mockk(relaxed = true)
        projectRepositoryImp = ProjectRepositoryImp(projectDataSource, userAssignedToProjectDataSource)
    }


    @Test
    fun `getAllProjects() should read from data source if cache is empty`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectDto))

        val result = projectRepositoryImp.getAllProjects()

        assertThat(result.getOrNull()).containsExactly(testProject)
    }

    @Test
    fun `getAllProjects() should return failure when read fails`() {
        every { projectDataSource.read() } returns Result.failure(ReadDataException())

        val result = projectRepositoryImp.getAllProjects()

        assertThat(result.isFailure).isTrue()

    }

    @Test
    fun `addProject() should write project to data source and return success`() {
        //Given
        every { projectDataSource.append(listOf()) } returns Result.success(true)
        //When
        val result = projectRepositoryImp.addProject(testProject)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `addProject() should return failure when write fails`() {
        //Given
        every { projectDataSource.append(listOf(projectDto)) } returns Result.failure(
            WriteDataException()
        )

        val result = projectRepositoryImp.addProject(testProject)

        assertThrows<WriteDataException> { result.getOrThrow() }

    }

    @Test
    fun `editProject() should update existing project and write to data source`() {
        //Given
        every { projectDataSource.read() } returns Result.success(listOf(projectDto))
        every { projectDataSource.overWrite(listOf(projectDto)) } returns Result.success(true)

        //When
        val updated = testProject.copy(name = "Updated")
        val result = projectRepositoryImp.updateProject(updated)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `editProject() should fail when he can not read form data source`() {
        //Given
        every { projectDataSource.read() } returns Result.failure(ReadDataException())

        //When
        val result = projectRepositoryImp.updateProject(testProject)

        //When Then
        assertThrows<ReadDataException> { result.getOrThrow() }

    }


    @Test
    fun `editProject() should return failure when write fails`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectDto))
        every { projectDataSource.overWrite(listOf(projectDto)) } returns Result.failure(WriteDataException())

        val result = projectRepositoryImp.updateProject(testProject)

        assertThrows<WriteDataException> { result.getOrThrow() }
    }

    @Test
    fun `deleteProject() should remove from list and write to data source`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectDto))
        every { projectDataSource.overWrite(listOf(projectDto)) } returns Result.success(true)


        val result = projectRepositoryImp.deleteProject(testProject)

        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `deleteProject() should return failure when read fails`() {
        every { projectDataSource.read() } returns Result.failure(Exception("fail"))

        val result = projectRepositoryImp.deleteProject(testProject)

        assertThrows<ReadDataException> { result.getOrThrow() }
    }

    @Test
    fun `getAllProjects() should return cached list if not empty`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectDto))

        val result = projectRepositoryImp.getAllProjects()

        assertThat(result.getOrNull()).isEqualTo(listOf(testProject, anotherProject))
    }

}
