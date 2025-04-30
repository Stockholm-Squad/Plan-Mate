package org.example.data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import utils.buildProject
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
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
    @Order(2)
    fun `getAllProjects() should read from data source if cache is empty`() {
        every { dataSource.read() } returns Result.success(listOf(testProject))

        val result = repository.getAllProjects()

        assertThat(result.getOrNull()).containsExactly(testProject)
    }

    @Test
    @Order(1)
    fun `getAllProjects() should return failure when read fails`() {
        every { dataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        val result = repository.getAllProjects()

        assertThat(result.isFailure).isTrue()

    }

    @Test
    @Order(3)
    fun `addProject() should write project to data source and return success`() {
        every { dataSource.read() } returns Result.success(emptyList())
        every { dataSource.write(any()) } returns Result.success(true)

        val result = repository.addProject(testProject)

        assertThat(result.isSuccess).isTrue()
        verify { dataSource.write(match { it.contains(testProject) }) }
       // verify(exactly = 1) { dataSource.read() }
    }

    @Test
    @Order(4)
    fun `addProject() should return failure when write fails`() {
        every { dataSource.read() } returns Result.success(emptyList())
        every { dataSource.write(any()) } returns Result.failure(Exception("fail"))

        val result = repository.addProject(testProject)

        assertThat(result.isFailure).isTrue()

    }

    @Test
    @Order(5)
    fun `editProject() should update existing project and write to data source`() {
        every { dataSource.write(any()) } returns Result.success(true)

        // Preload with project
        repository.addProject(testProject)

        val updated = testProject.copy(name = "Updated")
        val result = repository.editProject(updated)

        assertThat(result.isSuccess).isTrue()

    }

    @Test
    @Order(5)
    fun `editProject() should return false when project not found`() {
        every { dataSource.write(any()) } returns Result.success(true)

        val result = repository.editProject(anotherProject)

        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `editProject() should return failure when write fails`() {
        every { dataSource.write(any()) } returns Result.failure(Exception("fail"))

        val result = repository.editProject(testProject)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    @Order(4)
    fun `deleteProject() should remove from list and write to data source`() {
        every { dataSource.read() } returns Result.success(listOf(testProject))
        every { dataSource.write(any()) } returns Result.success(true)

        repository.addProject(testProject)
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
    @Order(2)
    fun `getAllProjects() should return cached list if not empty`() {
        every { dataSource.write(any()) } returns Result.success(true)

        repository.addProject(testProject)
        val result = repository.getAllProjects()

        assertThat(result.isSuccess).isTrue()
    }


}
