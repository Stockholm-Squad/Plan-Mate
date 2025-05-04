//package data.repo
//
//import com.google.common.truth.Truth.assertThat
//import data.models.TaskInProject
//import data.models.UserAssignedToProject
//import io.mockk.*
//import org.example.data.datasources.project_data_source.IProjectDataSource
//import org.example.data.datasources.task_In_project_data_source.TaskInProjectCsvDataSource
//import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
//import org.example.data.models.ProjectModel
//import org.example.data.repo.ProjectRepositoryImp
//import org.example.logic.model.exceptions.ReadDataException
//import org.example.logic.model.exceptions.WriteDataException
//import org.junit.jupiter.api.*
//import utils.buildProject
//import kotlin.test.Test
//
//class ProjectRepositoryImpTest {
//
//    private lateinit var projectDataSource: IProjectDataSource
//    private lateinit var taskInProjectDataSource: TaskInProjectCsvDataSource
//    private lateinit var userAssignedToProjectDataSource: IUserAssignedToProjectDataSource
//    private lateinit var repository: ProjectRepositoryImp
//    private val testProject = buildProject(name = "Test Project")
//    private val anotherProject = buildProject(name = "Another Project")
//    private val testTaskInProject = TaskInProject(projectId = "1", taskId = "101")
//    private val taskInProjectWithDifferentTaskId = TaskInProject(projectId = "1", taskId = "102")
//    private val taskInProjectWithDifferentProjectId = TaskInProject(projectId = "2", taskId = "101")
//    private val testUserAssigned = UserAssignedToProject(projectId = "1", userName = "user1")
//    private val userAssignedWithDifferentUserName = UserAssignedToProject(projectId = "1", userName = "user2")
//    private val userAssignedWithDifferentProjectId = UserAssignedToProject(projectId = "2", userName = "user1")
//    private val projectModel = ProjectModel("1", "name", "12")
//
//    @BeforeEach
//    fun setUp() {
//        projectDataSource = mockk(relaxed = true)
//        taskInProjectDataSource = mockk(relaxed = true)
//        userAssignedToProjectDataSource = mockk(relaxed = true)
//        repository = ProjectRepositoryImp(projectDataSource, userAssignedToProjectDataSource)
//    }
//
//
////    @Test
////    fun `getAllProjects() should read from data source if cache is empty`() {
////        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
////
////        val result = repository.getAllProjects()
////
////        assertThat(result.getOrNull()).containsExactly(testProject)
////    }
////
////    @Test
////    fun `getAllProjects() should return failure when read fails`() {
////        every { projectDataSource.read() } returns Result.failure(ReadDataException())
////
////        val result = repository.getAllProjects()
////
////        assertThat(result.isFailure).isTrue()
////
////    }
////
////    @Test
////    fun `addProject() should write project to data source and return success`() {
////        //Given
////        every { projectDataSource.append(listOf()) } returns Result.success(true)
////        //When
////        val result = repository.addProject(testProject)
////        //Then
////        assertThat(result.isSuccess).isTrue()
////
////    }
////
////    @Test
////    fun `addProject() should return failure when write fails`() {
////        //Given
////        every { projectDataSource.append(listOf(projectModel)) } returns Result.failure(
////            WriteDataException()
////        )
////
////        val result = repository.addProject(testProject)
////
////        assertThrows<WriteDataException> { result.getOrThrow() }
////
////    }
////
////    @Test
////    fun `editProject() should update existing project and write to data source`() {
////        //Given
////        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
////        every { projectDataSource.overWrite(listOf(projectModel)) } returns Result.success(true)
////
////        //When
////        val updated = testProject.copy(name = "Updated")
////        val result = repository.editProject(updated)
////        //Then
////        assertThat(result.isSuccess).isTrue()
////
////    }
////
////    @Test
////    fun `editProject() should fail when he can not read form data source`() {
////        //Given
////        every { projectDataSource.read() } returns Result.failure(ReadDataException())
////
////        //When
////        val result = repository.editProject(testProject)
////
////        //When Then
////        assertThrows<ReadDataException> { result.getOrThrow() }
////
////    }
////
////
////    @Test
////    fun `editProject() should return failure when write fails`() {
////        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
////        every { projectDataSource.overWrite(listOf(projectModel)) } returns Result.failure(WriteDataException())
////
////        val result = repository.editProject(testProject)
////
////        assertThrows<WriteDataException> { result.getOrThrow() }
////    }
////
////    @Test
////    fun `deleteProject() should remove from list and write to data source`() {
////        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
////        every { projectDataSource.overWrite(listOf(projectModel)) } returns Result.success(true)
////
////
////        val result = repository.deleteProject(testProject)
////
////        assertThat(result.isSuccess).isTrue()
////
////    }
//
////    @Test
////    fun `deleteProject() should return failure when read fails`() {
////        every { projectDataSource.read() } returns Result.failure(Exception("fail"))
////
////        val result = repository.deleteProject(testProject)
////
////        assertThrows<ReadDataException> { result.getOrThrow() }
////    }
////
////    @Test
////    fun `getAllProjects() should return cached list if not empty`() {
////        every { projectDataSource.read() } returns Result.success(listOf(projectModel))
////
////        val result = repository.getAllProjects()
////
////        assertThat(result.getOrNull()).isEqualTo(listOf(testProject, anotherProject))
////    }
////
////    @Nested
////    inner class TasksInProjectTests {
////
////        @Test
////        fun `getTasksInProject should return list of task IDs for given project`() {
////            every { taskInProjectDataSource.read() } returns Result.success(
////                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
////            )
////
////            val result = repository.getTasksInProject("1")
////
////            assertThat(result.getOrNull()).containsExactly("101", "102")
////        }
////
////        @Test
////        fun `getTasksInProject should return empty list when no tasks for project`() {
////            every { taskInProjectDataSource.read() } returns Result.success(
////                listOf(TaskInProject(projectId = "2", taskId = "201"))
////            )
////
////            val result = repository.getTasksInProject("1")
////
////            assertThat(result.getOrNull()).isEmpty()
////        }
////
////        @Test
////        fun `getTasksInProject should return failure when read fails`() {
////            every { taskInProjectDataSource.read() } returns Result.failure(
////                ReadDataException()
////            )
////
////            val result = repository.getTasksInProject("1")
////
////            assertThrows < ReadDataException() > { result.getOrThrow() }
////        }
////
////        @Test
////        fun `addTaskInProject should append task and return success`() {
////            every { taskInProjectDataSource.append(any()) } returns Result.success(true)
////
////            val result = repository.addTaskInProject("1", "101")
////
////            assertThat(result.getOrThrow()).isTrue()
////            verify { taskInProjectDataSource.append(listOf(TaskInProject(projectId = "1", taskId = "101"))) }
////        }
////
////        @Test
////        fun `addTaskInProject should return failure when write fails`() {
////            every { taskInProjectDataSource.append(any()) } returns Result.failure(
////                WriteDataException()
////            )
////
////            val result = repository.addTaskInProject("1", "101")
////
////            assertThrows<WriteDataException> { result.getOrThrow() }
////        }
////
////        @Test
////        fun `deleteTaskFromProject should remove task and return success`() {
////            every { taskInProjectDataSource.read() } returns Result.success(
////                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
////            )
////            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)
////
////            val result = repository.deleteTaskFromProject("1", "101")
////
////            assertThat(result.isSuccess).isTrue()
////            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentTaskId)) }
////        }
////
////        @Test
////        fun `deleteTaskFromProject should return success when task not found`() {
////            every { taskInProjectDataSource.read() } returns Result.success(
////                listOf(taskInProjectWithDifferentTaskId)
////            )
////            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)
////
////            val result = repository.deleteTaskFromProject("1", "101")
////
////            assertThat(result.isSuccess).isTrue()
////            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentTaskId)) }
////        }
////
////        @Test
////        fun `deleteTaskFromProject should return success when project not found`() {
////            every { taskInProjectDataSource.read() } returns Result.success(
////                listOf(taskInProjectWithDifferentProjectId)
////            )
////            every { taskInProjectDataSource.overWrite(any()) } returns Result.success(true)
////
////            val result = repository.deleteTaskFromProject("1", "101")
////
////            assertThat(result.isSuccess).isTrue()
////            verify { taskInProjectDataSource.overWrite(listOf(taskInProjectWithDifferentProjectId)) }
////        }
////
////        @Test
////        fun `deleteTaskFromProject should return failure when read fails`() {
////            every { taskInProjectDataSource.read() } returns Result.failure(
////                ReadDataException()
////            )
////
////            val result = repository.deleteTaskFromProject("1", "101")
////
////            assertThrows<ReadDataException> { result.getOrThrow() }
////        }
////
////        @Test
////        fun `deleteTaskFromProject should return failure when write fails`() {
////            every { taskInProjectDataSource.read() } returns Result.success(
////                listOf(testTaskInProject, taskInProjectWithDifferentTaskId)
////            )
////            every { taskInProjectDataSource.overWrite(any()) } returns Result.failure(
////                WriteDataException()
////            )
////
////            val result = repository.deleteTaskFromProject("1", "101")
////
////            assertThrows < WriteDataException() > { result.getOrThrow() }
////        }
////    }
////
////    @Nested
////    inner class UsersAssignedToProjectTests {
////        @Test
////        fun `getUsersAssignedToProject should return list of usernames for given project`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.success(
////                listOf(testUserAssigned, userAssignedWithDifferentUserName)
////            )
////
////            val result = repository.getUsersAssignedToProject("1")
////
////            assertThat(result.getOrNull()).containsExactly("user1", "user2")
////        }
////
////        @Test
////        fun `getUsersAssignedToProject should return empty list when no users for project`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.success(
////                listOf(UserAssignedToProject(projectId = "2", userName = "user3"))
////            )
////
////            val result = repository.getUsersAssignedToProject("1")
////
////            assertThat(result.getOrNull()).isEmpty()
////        }
////
////        @Test
////        fun `getUsersAssignedToProject should return failure when read fails`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.failure(
////                ReadDataException()
////            )
////
////            val result = repository.getUsersAssignedToProject("1")
////
////            assertThrows<ReadDataException> { result.getOrThrow() }
////        }
////
////        @Test
////        fun `addUserAssignedToProject should append user and return success`() {
////            every { userAssignedToProjectDataSource.append(any()) } returns Result.success(true)
////
////            val result = repository.addUserAssignedToProject("1", "user1")
////
////            assertThat(result.isSuccess).isTrue()
////            verify {
////                userAssignedToProjectDataSource.append(
////                    listOf(UserAssignedToProject(projectId = "1", userName = "user1"))
////                )
////            }
////        }
////
////        @Test
////        fun `addUserAssignedToProject should return failure when write fails`() {
////            every { userAssignedToProjectDataSource.append(any()) } returns Result.failure(
////                WriteDataException()
////            )
////
////            val result = repository.addUserAssignedToProject("1", "user1")
////
////            assertThrows<WriteDataException> { result.getOrThrow() }
////        }
////
////        @Test
////        fun `deleteUserAssignedToProject should remove user and return success`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.success(
////                listOf(testUserAssigned, userAssignedWithDifferentUserName)
////            )
////            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.success(true)
////
////            val result = repository.deleteUserAssignedToProject("1", "user1")
////
////            assertThat(result.isSuccess).isTrue()
////            verify { userAssignedToProjectDataSource.overWrite(listOf(userAssignedWithDifferentUserName)) }
////        }
////
////        @Test
////        fun `deleteUserAssignedToProject should return success when user not found`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.success(
////                listOf(userAssignedWithDifferentUserName)
////            )
////            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.success(true)
////
////            val result = repository.deleteUserAssignedToProject("1", "user1")
////
////            assertThat(result.isSuccess).isTrue()
////            verify { userAssignedToProjectDataSource.overWrite(listOf(userAssignedWithDifferentUserName)) }
////        }
////
////        @Test
////        fun `deleteUserAssignedToProject should return success when project not found`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.success(
////                listOf(userAssignedWithDifferentProjectId)
////            )
////            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.success(true)
////
////            val result = repository.deleteUserAssignedToProject("1", "user1")
////
////            assertThat(result.isSuccess).isTrue()
////            verify { userAssignedToProjectDataSource.overWrite(listOf(userAssignedWithDifferentProjectId)) }
////        }
////
////        @Test
////        fun `deleteUserAssignedToProject should return failure when read fails`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.failure(
////                ReadDataException()
////            )
////
////            val result = repository.deleteUserAssignedToProject("1", "user1")
////
////            assertThrows<ReadDataException> { result.getOrThrow() }
////        }
////
////        @Test
////        fun `deleteUserAssignedToProject should return failure when write fails`() {
////            every { userAssignedToProjectDataSource.read() } returns Result.success(
////                listOf(testUserAssigned, userAssignedWithDifferentUserName)
////            )
////            every { userAssignedToProjectDataSource.overWrite(any()) } returns Result.failure(
////                WriteDataException()
////            )
////
////            val result = repository.deleteUserAssignedToProject("1", "user1")
////
////            assertThrows<WriteDataException> { result.getOrThrow() }
////        }
////    }
//}
