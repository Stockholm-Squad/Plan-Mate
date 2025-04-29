package data.repo

import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Project
import org.example.data.datasources.CsvDataSource
import org.example.data.datasources.PlanMateDataSource
import org.example.data.repo.ProjectRepositoryImp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import utils.buildProject
import kotlin.test.Test

class ProjectRepositoryImpTest{


  private lateinit var projectDataSource: PlanMateDataSource<Project>
  private lateinit var projectRepository: ProjectRepositoryImp

  @BeforeEach
  fun setup() {
   projectDataSource = mockk(relaxed = true)
   projectRepository = ProjectRepositoryImp(projectDataSource)
  }

  @Test
  fun `getProjectById returns success`() {
   val project = buildProject(id = "1", name = "Test Project")
   every { projectRepository.getProjectById("1") } returns Result.success(project)

   val result = projectRepository.getProjectById("1")

   assertThat(result.isSuccess).isTrue()
   assertThat(result.getOrNull()).isEqualTo(project)
   verify { projectRepository.getProjectById("1") }
  }

  @Test
  fun `getProjectById returns failure`() {
   val exception = Exception("Project not found")
   every { projectRepository.getProjectById("1") } returns Result.failure(exception)

   val result = projectRepository.getProjectById("1")

   assertThat(result.isFailure).isTrue()
   assertThat(result.exceptionOrNull()).isEqualTo(exception)
   verify { projectRepository.getProjectById("1") }
  }

  @Test
  fun `addProject returns success`() {
   val project = buildProject(id = "1", name = "New Project")
   every { projectRepository.addProject(project) } returns Result.success(true)

   val result = projectRepository.addProject(project)

   assertThat(result.isSuccess).isTrue()
   assertThat(result.getOrNull()).isTrue()
   verify { projectRepository.addProject(project) }
  }

  @Test
  fun `addProject returns failure`() {
   val project = buildProject(id = "1", name = "New Project")
   val exception = Exception("Failed to add")
   every { projectRepository.addProject(project) } returns Result.failure(exception)

   val result = projectRepository.addProject(project)

   assertThat(result.isFailure).isTrue()
   assertThat(result.exceptionOrNull()).isEqualTo(exception)
   verify { projectRepository.addProject(project) }
  }

  @Test
  fun `editProject returns success`() {
   val project = buildProject(id = "1", name = "Updated Project")
   every { projectRepository.editProject(project) } returns Result.success(true)

   val result = projectRepository.editProject(project)

   assertThat(result.isSuccess).isTrue()
   assertThat(result.getOrNull()).isTrue()
   verify { projectRepository.editProject(project) }
  }

  @Test
  fun `editProject returns failure`() {
   val project = buildProject(id = "1", name = "Updated Project")
   val exception = Exception("Failed to edit")
   every { projectRepository.editProject(project) } returns Result.failure(exception)

   val result = projectRepository.editProject(project)

   assertThat(result.isFailure).isTrue()
   assertThat(result.exceptionOrNull()).isEqualTo(exception)
   verify { projectRepository.editProject(project) }
  }

  @Test
  fun `deleteProject returns success`() {
   every { projectRepository.deleteProject("1") } returns Result.success(true)

   val result = projectRepository.deleteProject("1")

   assertThat(result.isSuccess).isTrue()
   assertThat(result.getOrNull()).isTrue()
   verify { projectRepository.deleteProject("1") }
  }

  @Test
  fun `deleteProject returns failure`() {
   val exception = Exception("Failed to delete")
   every { projectRepository.deleteProject("1") } returns Result.failure(exception)

   val result = projectRepository.deleteProject("1")

   assertThat(result.isFailure).isTrue()
   assertThat(result.exceptionOrNull()).isEqualTo(exception)
   verify { projectRepository.deleteProject("1") }
  }

  @Test
  fun `getAllProjects returns success`() {
   val projects = listOf(buildProject(id = "1", name = "P1"), buildProject(id = "2", name = "P2"))
   every { projectRepository.getAllProjects() } returns Result.success(projects)

   val result = projectRepository.getAllProjects()

   assertThat(result.isSuccess).isTrue()
   assertThat(result.getOrNull()).isEqualTo(projects)
   verify { projectRepository.getAllProjects() }
  }

  @Test
  fun `getAllProjects returns failure`() {
   val exception = Exception("Failed to fetch projects")
   every { projectRepository.getAllProjects() } returns Result.failure(exception)

   val result = projectRepository.getAllProjects()

   assertThat(result.isFailure).isTrue()
   assertThat(result.exceptionOrNull()).isEqualTo(exception)
   verify { projectRepository.getAllProjects() }
  }
 }