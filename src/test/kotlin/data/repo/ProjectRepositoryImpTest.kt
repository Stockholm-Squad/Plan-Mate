package org.example.data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.*
import utils.buildProject
import kotlin.test.Test

class ProjectRepositoryImpTest {

    private lateinit var dataSource: PlanMateDataSource<Project>
    private lateinit var repository: ProjectRepositoryImp
    private val testProject = buildProject(id = "1", name = "Test Project", stateId = "")
    private val anotherProject = buildProject(id = "2", name = "Another Project", stateId = "")

    @BeforeEach
    fun setUp() {
        dataSource = mockk(relaxed = true)
        repository = ProjectRepositoryImp(dataSource)
    }

    @Test
    fun `getAllProjects() should read from data source if cache is empty`() {
        every { dataSource.read() } returns Result.success(listOf(testProject))

        val result = repository.getAllProjects()

        assertThat(result.getOrNull()).containsExactly(testProject)
    }

    @Test
    fun `getAllProjects() should return failure when read fails`() {
        every { dataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        val result = repository.getAllProjects()

        assertThat(result.isFailure).isTrue()

    }

    @Test
    fun `addProject() should write project to data source and return success`() {
        //Given
        every { dataSource.append(listOf(testProject)) } returns Result.success(true)
        //When
        val result = repository.addProject(testProject)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `addProject() should return failure when write fails`() {
        //Given
        every { dataSource.append(listOf(testProject)) } returns Result.failure(
            PlanMateExceptions.DataException.WriteException()
        )

        val result = repository.addProject(testProject)

        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }

    }

    @Test
    fun `editProject() should update existing project and write to data source`() {
        //Given
        every { dataSource.read() } returns Result.success(listOf(testProject, anotherProject))
        every { dataSource.overWrite(listOf(testProject, anotherProject)) } returns Result.success(true)

        //When
        val updated = testProject.copy(name = "Updated")
        val result = repository.editProject(updated)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `editProject() should fail when he can not read form data source`() {
        //Given
        every { dataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        //When
        val result = repository.editProject(testProject)

        //When Then
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }

    }


    @Test
    fun `editProject() should return failure when write fails`() {
        every { dataSource.read() } returns Result.success(listOf(testProject))
        every { dataSource.overWrite(listOf(testProject)) } returns Result.failure(PlanMateExceptions.DataException.WriteException())

        val result = repository.editProject(testProject)

        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
    }

    @Test
    fun `deleteProject() should remove from list and write to data source`() {
        every { dataSource.read() } returns Result.success(listOf(testProject))
        every { dataSource.overWrite(listOf(testProject, anotherProject)) } returns Result.success(true)


        val result = repository.deleteProject(testProject)

        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `deleteProject() should return failure when read fails`() {
        every { dataSource.read() } returns Result.failure(Exception("fail"))

        val result = repository.deleteProject(testProject)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.DataException.ReadException::class.java)
    }

    @Test
    fun `getAllProjects() should return cached list if not empty`() {
        every { dataSource.read() } returns Result.success(listOf(testProject, anotherProject))

        val result = repository.getAllProjects()

        assertThat(result.getOrNull()).isEqualTo(listOf(testProject, anotherProject))
    }


}
