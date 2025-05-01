package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.data.entities.TaskInProject
import org.example.data.entities.UserAssignedToProject
import org.example.data.repo.ProjectRepositoryImp
import org.example.logic.model.exceptions.PlanMateExceptions
import org.junit.jupiter.api.*
import utils.buildProject
import kotlin.test.Test

class ProjectRepositoryImpTest {

    private lateinit var projectDataSource: PlanMateDataSource<Project>
    private lateinit var taskInProjectDataSource: PlanMateDataSource<TaskInProject>
    private lateinit var userAssignedToProjectDataSource: PlanMateDataSource<UserAssignedToProject>
    private lateinit var repository: ProjectRepositoryImp
    private val testProject = buildProject(id = "1", name = "Test Project", stateId = "")
    private val anotherProject = buildProject(id = "2", name = "Another Project", stateId = "")
    private val testTaskInProject = TaskInProject(projectId = "1", taskId = "101")
    private val taskInProjectWithDifferentTaskId = TaskInProject(projectId = "1", taskId = "102")
    private val taskInProjectWithDifferentProjectId = TaskInProject(projectId = "2", taskId = "101")
    private val testUserAssigned = UserAssignedToProject(projectId = "1", userName = "user1")
    private val userAssignedWithDifferentUserName = UserAssignedToProject(projectId = "1", userName = "user2")
    private val userAssignedWithDifferentProjectId = UserAssignedToProject(projectId = "2", userName = "user1")

    @BeforeEach
    fun setUp() {
        projectDataSource = mockk(relaxed = true)
        taskInProjectDataSource = mockk(relaxed = true)
        userAssignedToProjectDataSource = mockk(relaxed = true)
        repository = ProjectRepositoryImp(projectDataSource, taskInProjectDataSource, userAssignedToProjectDataSource)
    }


    @Test
    fun `getAllProjects() should read from data source if cache is empty`() {
        every { projectDataSource.read() } returns Result.success(listOf(testProject))

        val result = repository.getAllProjects()

        assertThat(result.getOrNull()).containsExactly(testProject)
    }

    @Test
    fun `getAllProjects() should return failure when read fails`() {
        every { projectDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        val result = repository.getAllProjects()

        assertThat(result.isFailure).isTrue()

    }

    @Test
    fun `addProject() should write project to data source and return success`() {
        //Given
        every { projectDataSource.append(listOf(testProject)) } returns Result.success(true)
        //When
        val result = repository.addProject(testProject)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `addProject() should return failure when write fails`() {
        //Given
        every { projectDataSource.append(listOf(testProject)) } returns Result.failure(
            PlanMateExceptions.DataException.WriteException()
        )

        val result = repository.addProject(testProject)

        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }

    }

    @Test
    fun `editProject() should update existing project and write to data source`() {
        //Given
        every { projectDataSource.read() } returns Result.success(listOf(testProject, anotherProject))
        every { projectDataSource.overWrite(listOf(testProject, anotherProject)) } returns Result.success(true)

        //When
        val updated = testProject.copy(name = "Updated")
        val result = repository.editProject(updated)
        //Then
        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `editProject() should fail when he can not read form data source`() {
        //Given
        every { projectDataSource.read() } returns Result.failure(PlanMateExceptions.DataException.ReadException())

        //When
        val result = repository.editProject(testProject)

        //When Then
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }

    }


    @Test
    fun `editProject() should return failure when write fails`() {
        every { projectDataSource.read() } returns Result.success(listOf(testProject))
        every { projectDataSource.overWrite(listOf(testProject)) } returns Result.failure(PlanMateExceptions.DataException.WriteException())

        val result = repository.editProject(testProject)

        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
    }

    @Test
    fun `deleteProject() should remove from list and write to data source`() {
        every { projectDataSource.read() } returns Result.success(listOf(testProject))
        every { projectDataSource.overWrite(listOf(testProject, anotherProject)) } returns Result.success(true)


        val result = repository.deleteProject(testProject)

        assertThat(result.isSuccess).isTrue()

    }

    @Test
    fun `deleteProject() should return failure when read fails`() {
        every { projectDataSource.read() } returns Result.failure(Exception("fail"))

        val result = repository.deleteProject(testProject)

        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `getAllProjects() should return cached list if not empty`() {
        every { projectDataSource.read() } returns Result.success(listOf(testProject, anotherProject))

        val result = repository.getAllProjects()

        assertThat(result.getOrNull()).isEqualTo(listOf(testProject, anotherProject))
    }

    @Nested
    inner class TasksInProjectTests {

        @Test
        fun `getTasksInProject should return list of task IDs for given project`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
            )

            val result = repository.getTasksInProject("1")

            assertThat(result.getOrNull()).containsExactly("101", "102")
        }

        @Test
        fun `getTasksInProject should return empty list when no tasks for project`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(TaskInProject(projectId = "2", taskId = "201"))
            )

            val result = repository.getTasksInProject("1")

            assertThat(result.getOrNull()).isEmpty()
        }

        @Test
        fun `getTasksInProject should return failure when read fails`() {
            every { taskInProjectDataSource.read() } returns Result.failure(
                PlanMateExceptions.DataException.ReadException()
            )

            val result = repository.getTasksInProject("1")

            assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
        }

        @Test
        fun `addTaskInProject should append task and return success`() {
            every { taskInProjectDataSource.append(any()) } returns Result.success(true)

            val result = repository.addTaskInProject("1", "101")

            assertThat(result.getOrThrow()).isTrue()
            verify { taskInProjectDataSource.append(listOf(TaskInProject(projectId = "1", taskId = "101"))) }
        }

        @Test
        fun `addTaskInProject should return failure when write fails`() {
            every { taskInProjectDataSource.append(any()) } returns Result.failure(
                PlanMateExceptions.DataException.WriteException()
            )

            val result = repository.addTaskInProject("1", "101")

            assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
        }

        @Test
        fun `deleteTaskFromProject should remove task and return success`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = repository.deleteTaskFromProject("1", "101")

            assertThat(result.isSuccess).isTrue()
            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentTaskId)) }
        }

        @Test
        fun `deleteTaskFromProject should return success when task not found`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(taskInProjectWithDifferentTaskId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = repository.deleteTaskFromProject("1", "101")

            assertThat(result.isSuccess).isTrue()
            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentTaskId)) }
        }

        @Test
        fun `deleteTaskFromProject should return success when project not found`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(taskInProjectWithDifferentProjectId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = repository.deleteTaskFromProject("1", "101")

            assertThat(result.isSuccess).isTrue()
            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentProjectId)) }
        }

        @Test
        fun `deleteTaskFromProject should return failure when read fails`() {
            every { taskInProjectDataSource.read() } returns Result.failure(
                PlanMateExceptions.DataException.ReadException()
            )

            val result = repository.deleteTaskFromProject("1", "101")

            assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
        }

        @Test
        fun `deleteTaskFromProject should return failure when write fails`() {
            every { taskInProjectDataSource.read() } returns Result.success(
                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
            )
            every { taskInProjectDataSource.overWrite(any()) } returns Result.failure(
                PlanMateExceptions.DataException.WriteException()
            )

            val result = repository.deleteTaskFromProject("1", "101")

            assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
        }
    }

    @Nested
    inner class UsersAssignedToProjectTests {
        @Test
        fun `getUsersAssignedToProject should return list of usernames for given project`() {
            every { userAssignedToProjectDataSource.read() } returns Result.success(
                listOf(testUserAssigned, userAssignedWithDifferentUserName)
            )

            val result = repository.getUsersAssignedToProject("1")

            assertThat(result.getOrNull()).containsExactly("user1", "user2")
        }

        @Test
        fun `getUsersAssignedToProject should return empty list when no users for project`() {
            every { userAssignedToProjectDataSource.read() } returns Result.success(
                listOf(UserAssignedToProject(projectId = "2", userName = "user3"))
            )

            val result = repository.getUsersAssignedToProject("1")

            assertThat(result.getOrNull()).isEmpty()
        }

        @Test
        fun `getUsersAssignedToProject should return failure when read fails`() {
            every { userAssignedToProjectDataSource.read() } returns Result.failure(
                PlanMateExceptions.DataException.ReadException()
            )

            val result = repository.getUsersAssignedToProject("1")

            assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
        }

        @Test
        fun `addUserAssignedToProject should append user and return success`() {
            every { userAssignedToProjectDataSource.append(any()) } returns Result.success(true)

            val result = repository.addUserAssignedToProject("1", "user1")

            assertThat(result.isSuccess).isTrue()
            verify {
                userAssignedToProjectDataSource.append(
                    listOf(UserAssignedToProject(projectId = "1", userName = "user1"))
                )
            }
        }

        @Test
        fun `addUserAssignedToProject should return failure when write fails`() {
            every { userAssignedToProjectDataSource.append(any()) } returns Result.failure(
                PlanMateExceptions.DataException.WriteException()
            )

            val result = repository.addUserAssignedToProject("1", "user1")

            assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
        }

        @Test
        fun `deleteUserAssignedToProject should remove user and return success`() {
            every { userAssignedToProjectDataSource.read() } returns Result.success(
                listOf(testUserAssigned, userAssignedWithDifferentUserName)
            )
            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = repository.deleteUserAssignedToProject("1", "user1")

            assertThat(result.isSuccess).isTrue()
            verify { userAssignedToProjectDataSource.overWrite(listOf(userAssignedWithDifferentUserName)) }
        }

        @Test
        fun `deleteUserAssignedToProject should return success when user not found`() {
            every { userAssignedToProjectDataSource.read() } returns Result.success(
                listOf(userAssignedWithDifferentUserName)
            )
            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = repository.deleteUserAssignedToProject("1", "user1")

            assertThat(result.isSuccess).isTrue()
            verify { userAssignedToProjectDataSource.overWrite(listOf(userAssignedWithDifferentUserName)) }
        }

        @Test
        fun `deleteUserAssignedToProject should return success when project not found`() {
            every { userAssignedToProjectDataSource.read() } returns Result.success(
                listOf(userAssignedWithDifferentProjectId)
            )
            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.success(true)

            val result = repository.deleteUserAssignedToProject("1", "user1")

            assertThat(result.isSuccess).isTrue()
            verify { userAssignedToProjectDataSource.overWrite(listOf(userAssignedWithDifferentProjectId)) }
        }

        @Test
        fun `deleteUserAssignedToProject should return failure when read fails`() {
            every { userAssignedToProjectDataSource.read() } returns Result.failure(
                PlanMateExceptions.DataException.ReadException()
            )

            val result = repository.deleteUserAssignedToProject("1", "user1")

            assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
        }

        @Test
        fun `deleteUserAssignedToProject should return failure when write fails`() {
            every { userAssignedToProjectDataSource.read() } returns Result.success(
                listOf(testUserAssigned, userAssignedWithDifferentUserName)
            )
            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.failure(
                PlanMateExceptions.DataException.WriteException()
            )

            val result = repository.deleteUserAssignedToProject("1", "user1")

            assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
        }
    }
}
