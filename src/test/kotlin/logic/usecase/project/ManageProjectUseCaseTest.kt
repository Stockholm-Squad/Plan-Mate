package org.example.logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Project
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ManageProjectUseCaseTest {

    private lateinit var repository: ProjectRepository
    private lateinit var useCase: ManageProjectUseCase

    private val testProject = Project(id = "1", name = "Test Project", stateId = "")

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = ManageProjectUseCase(repository)
    }

    @Test
    fun `getAllProjects returns success when repository returns list`() {
        every { repository.getAllProjects() } returns Result.success(listOf(testProject))

        val result = useCase.getAllProjects()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).containsExactly(testProject)
    }

    @Test
    fun `getAllProjects returns failure when repository fails`() {
        every { repository.getAllProjects() } returns Result.failure(Exception("Fail"))

        val result = useCase.getAllProjects()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.LogicException.NoObjectFound::class.java)
    }

    @Test
    fun `getProjectById returns project when found`() {
        every { repository.getAllProjects() } returns Result.success(listOf(testProject))

        val result = useCase.getProjectById("1")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testProject)
    }

    @Test
    fun `getProjectById returns failure when repository fails`() {
        every { repository.getAllProjects() } returns Result.failure(Exception("Fail"))

        val result = useCase.getProjectById("1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.LogicException.NoObjectFound::class.java)
    }

    @Test
    fun `getProjectById returns failure when project not found`() {
        every { repository.getAllProjects() } returns Result.success(emptyList())

        val result = useCase.getProjectById("1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.LogicException.NoObjectFound::class.java)
    }

    @Test
    fun `addProject returns success when added`() {
        every { repository.addProject(testProject) } returns Result.success(true)

        val result = useCase.addProject(testProject)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `addProject returns failure when repository fails`() {
        every { repository.addProject(testProject) } returns Result.failure(Exception("fail"))

        val result = useCase.addProject(testProject)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.LogicException.NoProjectAdded::class.java)
    }

    @Test
    fun `updateProject returns success and calls repository`() {
        every { repository.getAllProjects() } returns Result.success(listOf(testProject))
        every { repository.editProject(testProject) } returns Result.success(true)

        val result = useCase.updateProject("1")

        assertThat(result.isSuccess).isTrue()
        verify { repository.editProject(testProject) }
    }

    @Test
    fun `updateProject returns failure when project not found`() {
        every { repository.getAllProjects() } returns Result.success(emptyList())

        val result = useCase.updateProject("1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.LogicException.NoObjectFound::class.java)
    }

    @Test
    fun `removeProjectById returns success and calls repository`() {
        every { repository.getAllProjects() } returns Result.success(listOf(testProject))
        every { repository.deleteProject(testProject) } returns Result.success(true)

        val result = useCase.removeProjectById("1")

        assertThat(result.isSuccess).isTrue()
        verify { repository.deleteProject(testProject) }
    }

    @Test
    fun `removeProjectById returns failure when project not found`() {
        every { repository.getAllProjects() } returns Result.success(emptyList())

        val result = useCase.removeProjectById("1")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.LogicException.NoObjectFound::class.java)
    }
}
