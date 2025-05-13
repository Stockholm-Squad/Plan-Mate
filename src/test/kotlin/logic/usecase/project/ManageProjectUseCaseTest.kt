package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.NoEntityStateFoundException
import org.example.logic.NoProjectDeletedException
import org.example.logic.ProjectAlreadyExistException
import org.example.logic.ProjectNotFoundException
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildProject
import java.util.*

class ManageProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        manageStatesUseCase = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        manageProjectUseCase = ManageProjectUseCase(projectRepository, manageStatesUseCase, getProjectsUseCase)
    }

    @Nested
    inner class AddProject {
        @Test
        fun `addProject() should return true when project is added successfully`() = runTest {
            val projectName = "New Project"
            val stateName = "todo"
            val stateId = UUID.randomUUID()

            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws ProjectNotFoundException()
            coEvery { manageStatesUseCase.getEntityStateIdByName(stateName) } returns stateId
            coEvery { projectRepository.addProject(any()) } returns true

            val result = manageProjectUseCase.addProject(projectName, stateName)

            assertThat(result).isTrue()
            coVerify {
                projectRepository.addProject(withArg {
                    assertThat(it.title).isEqualTo(projectName)
                    assertThat(it.stateId).isEqualTo(stateId)
                })
            }
        }

        @Test
        fun `addProject() should throw ProjectAlreadyExistException when project name already exists`() = runTest {
            val projectName = "Existing"
            val existingProject = buildProject(name = projectName)

            coEvery { getProjectsUseCase.getProjectByName(projectName) } returns existingProject

            assertThrows<ProjectAlreadyExistException> {
                manageProjectUseCase.addProject(projectName, "todo")
            }
        }

        @Test
        fun `addProject() should return false when project already exists`() = runTest {
            //Given
            val projectName = "New Project"
            val stateName = "todo"
            val stateId = UUID.randomUUID()

            coEvery { getProjectsUseCase.getProjectByName(projectName) } throws ProjectNotFoundException()
            coEvery { manageStatesUseCase.getEntityStateIdByName(stateName) } returns stateId
            coEvery { projectRepository.addProject(any()) } returns false
            //When
            val result = manageProjectUseCase.addProject(projectName, stateName)
            //Then
            assertThat(result).isFalse()

        }


    }

    @Nested
    inner class UpdateProject {
        @Test
        fun `updateProject should return true when project exists and updates successfully`() = runTest {
            val projectId = UUID.randomUUID()
            val existingProject = buildProject(id = projectId)
            val updatedName = "Updated Project"
            val newStateId = UUID.randomUUID()

            coEvery { getProjectsUseCase.getAllProjects() } returns listOf(existingProject)
            coEvery { manageStatesUseCase.getEntityStateIdByName("done") } returns newStateId
            coEvery { projectRepository.updateProject(any()) } returns true

            val result = manageProjectUseCase.updateProject(projectId, updatedName, "done")

            assertThat(result).isTrue()
            coVerify {
                projectRepository.updateProject(withArg {
                    assertThat(it.id).isEqualTo(projectId)
                    assertThat(it.title).isEqualTo(updatedName)
                    assertThat(it.stateId).isEqualTo(newStateId)
                })
            }
        }

        @Test
        fun `updateProject should throw ProjectNotFoundException when project is missing`() = runTest {
            //Given
            coEvery { getProjectsUseCase.getAllProjects() } returns emptyList()
            //When & Then
            assertThrows<ProjectNotFoundException> {
                manageProjectUseCase.updateProject(UUID.randomUUID(), "Updated", "done")
            }
        }

        @Test
        fun `updateProject should throw ProjectNotFoundException when stateId is missing`() = runTest {
            //Given
            val id = UUID.randomUUID()
            coEvery { getProjectsUseCase.getAllProjects() } returns listOf(buildProject(id = id, name = "plan-mate"))
            coEvery { manageStatesUseCase.getEntityStateIdByName("todo") } throws NoEntityStateFoundException()
            coEvery { projectRepository.updateProject(buildProject(id = id, name = "plan-mate")) } returns false
            //When & Then
            assertThrows<ProjectNotFoundException> {
                manageProjectUseCase.updateProject(UUID.randomUUID(), "Updated", "done")
            }
        }

    }


    @Nested
    inner class RemoveProjectByName {
        @Test
        fun `removeProjectByName should return true when deletion succeeds`() = runTest {
            val project = buildProject(name = "ToRemove")

            coEvery { getProjectsUseCase.getProjectByName("ToRemove") } returns project
            coEvery { projectRepository.deleteProject(project) } returns true

            val result = manageProjectUseCase.removeProjectByName("ToRemove")

            assertThat(result).isTrue()
            coVerify { projectRepository.deleteProject(project) }
        }

        @Test
        fun `removeProjectByName should throw when deletion fails`() = runTest {
            val project = buildProject(name = "ToRemove")

            coEvery { getProjectsUseCase.getProjectByName("ToRemove") } returns project
            coEvery { projectRepository.deleteProject(project) } throws NoProjectDeletedException()

            assertThrows<NoProjectDeletedException> {
                manageProjectUseCase.removeProjectByName("ToRemove")
            }
        }
    }

    @Nested
    inner class IsProjectNameExists {
        @Test
        fun `isProjectNameExists should return true when project exists`() = runTest {
            val project = buildProject(name = "Exists")

            coEvery { getProjectsUseCase.getProjectByName("Exists") } returns project

            val result = manageProjectUseCase.isProjectNameExists("Exists")
            assertThat(result).isTrue()
        }

        @Test
        fun `isProjectNameExists should return false when project does not exist`() = runTest {
            coEvery { getProjectsUseCase.getProjectByName("NotFound") } throws ProjectNotFoundException()

            val result = manageProjectUseCase.isProjectNameExists("NotFound")
            assertThat(result).isFalse()
        }
    }

}