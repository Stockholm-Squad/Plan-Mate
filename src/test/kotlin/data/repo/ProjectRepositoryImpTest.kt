package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.example.data.datasources.project_data_source.IProjectDataSource
import org.example.data.datasources.task_In_project_data_source.TaskInProjectCsvDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.models.ProjectModel
import org.example.data.repo.ProjectRepositoryImp
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.model.exceptions.WriteDataException
import org.junit.jupiter.api.*
import utils.buildProject
import kotlin.test.Test

class ProjectRepositoryImpTest {

    private lateinit var projectDataSource: IProjectDataSource
    private lateinit var taskInProjectDataSource: TaskInProjectCsvDataSource
    private lateinit var userAssignedToProjectDataSource: IUserAssignedToProjectDataSource
    private lateinit var projectRepositoryImp: ProjectRepositoryImp
    private val testProject = buildProject(name = "Test Project")
    private val anotherProject = buildProject(name = "Another Project")
    private val projectModel = ProjectModel("1", "name", "12")

    @BeforeEach
    fun setUp() {
        projectDataSource = mockk(relaxed = true)
        taskInProjectDataSource = mockk(relaxed = true)
        userAssignedToProjectDataSource = mockk(relaxed = true)
        projectRepositoryImp = ProjectRepositoryImp(projectDataSource, userAssignedToProjectDataSource)
    }


    @Test
    fun `getAllProjects() should read from data source if cache is empty`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectModel))

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
        every { projectDataSource.append(listOf(projectModel)) } returns Result.failure(
            WriteDataException()
        )

        val result = projectRepositoryImp.addProject(testProject)

        assertThrows<WriteDataException> { result.getOrThrow() }

    }

    @Test
    fun `editProject() should update existing project and write to data source`() {
        //Given
        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
        every { projectDataSource.overWrite(listOf(projectModel)) } returns Result.success(true)

        //When
        val updated = testProject.copy(name = "Updated")
        val result = projectRepositoryImp.editProjectState(updated)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `editProject() should fail when he can not read form data source`() {
        //Given
        every { projectDataSource.read() } returns Result.failure(ReadDataException())

        //When
        val result = projectRepositoryImp.editProjectState(testProject)

        //When Then
        assertThrows<ReadDataException> { result.getOrThrow() }

    }


    @Test
    fun `editProject() should return failure when write fails`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
        every { projectDataSource.overWrite(listOf(projectModel)) } returns Result.failure(WriteDataException())

        val result = projectRepositoryImp.editProjectState(testProject)

        assertThrows<WriteDataException> { result.getOrThrow() }
    }

    @Test
    fun `deleteProject() should remove from list and write to data source`() {
        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
        every { projectDataSource.overWrite(listOf(projectModel)) } returns Result.success(true)


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
        every { projectDataSource.read() } returns Result.success(listOf(projectModel))

        val result = projectRepositoryImp.getAllProjects()

        assertThat(result.getOrNull()).isEqualTo(listOf(testProject, anotherProject))
    }

}
