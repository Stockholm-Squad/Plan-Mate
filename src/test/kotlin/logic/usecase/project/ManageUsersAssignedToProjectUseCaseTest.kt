package logic.usecase.project

import io.mockk.*
import logic.model.entities.Role
import logic.model.entities.User
import org.example.logic.repository.ProjectRepository
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ManageUsersAssignedToProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var userRepository: UserRepository
    private lateinit var useCase: ManageUsersAssignedToProjectUseCase
    private val testUser = User(username = "user1", hashedPassword = "user1@test.com")
    private val anotherUser = User(username = "user2", hashedPassword = "user2@test.com")

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        userRepository = mockk()
        useCase = ManageUsersAssignedToProjectUseCase(projectRepository, userRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUsersAssignedToProject should return users when successful`() {
        // Given
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user1", "user2"))
//        every { userRepository.getUserByUserName("user1") } returns Result.success(testUser)
//        every { userRepository.getUserByUserName("user2") } returns Result.success(anotherUser)
        every { userRepository.getAllUsers() } returns Result.success(
            listOf(testUser, anotherUser)
        )

        // When
        val result = useCase.getUsersAssignedToProject("1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(testUser, anotherUser), result.getOrNull())
    }

    @Test
    fun `getUsersAssignedToProject should filter out null users`() {
        // Given
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.success(
            listOf(
                "user1",
                "user2",
                "user3"
            )
        )
//        every { userRepository.getUserByUserName("user1") } returns Result.success(testUser)
//        every { userRepository.getUserByUserName("user2") } returns Result.failure(RuntimeException())
//        every { userRepository.getUserByUserName("user3") } returns Result.success(anotherUser)
        every { userRepository.getAllUsers() } returns Result.success(
            listOf(testUser, anotherUser)
        )


        // When
        val result = useCase.getUsersAssignedToProject("1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(testUser, anotherUser), result.getOrNull())
    }

    @Test
    fun `getUsersAssignedToProject should propagate project repository failure`() {
        // Given
        val expectedException = PlanMateExceptions.DataException.ReadException()
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.failure(expectedException)
        every { userRepository.getAllUsers() } returns Result.success(
            listOf(
                User(
                    username = "user1",
                    hashedPassword = "",
                    role = Role.MATE
                ),
                User(
                    username = "user2",
                    hashedPassword = "",
                    role = Role.MATE
                ),
                User(
                    username = "user3",
                    hashedPassword = "",
                    role = Role.MATE
                ),
            )
        )


        // When
        val result = useCase.getUsersAssignedToProject("1")

        // Then
        assertTrue(result.isFailure)
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `addUserAssignedToProject should return success when repository succeeds`() {
        // Given
        every { projectRepository.addUserAssignedToProject("1", "user1") } returns Result.success(true)

        // When
        val result = useCase.assignUserToProject("1", "user1")

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    @Test
    fun `addUserAssignedToProject should propagate failure`() {
        // Given
        val expectedException = PlanMateExceptions.DataException.WriteException()
        every { projectRepository.addUserAssignedToProject("1", "user1") } returns Result.failure(expectedException)

        // When
        val result = useCase.assignUserToProject("1", "user1")

        // Then
        assertTrue(result.isFailure)
        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
    }

    @Test
    fun `deleteUserAssignedToProject should return true when user exists and deletion succeeds`() {
        // Given
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user1", "user2"))
        every { projectRepository.deleteUserAssignedToProject("1", "user1") } returns Result.success(true)

        // When
        val result = useCase.deleteUserAssignedToProject("1", "user1")

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    @Test
    fun `deleteUserAssignedToProject should return false when user doesn't exist`() {
        // Given
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user2"))

        // When
        val result = useCase.deleteUserAssignedToProject("1", "user1")

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow())
    }

    @Test
    fun `deleteUserAssignedToProject should propagate read failure`() {
        // Given
        val expectedException = PlanMateExceptions.DataException.ReadException()
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.failure(expectedException)

        // When
        val result = useCase.deleteUserAssignedToProject("1", "user1")

        // Then
        assertTrue(result.isFailure)
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `deleteUserAssignedToProject should propagate delete failure`() {
        // Given
        every { projectRepository.getUsersAssignedToProject("1") } returns Result.success(listOf("user1"))
        val expectedException = PlanMateExceptions.DataException.WriteException()
        every { projectRepository.deleteUserAssignedToProject("1", "user1") } returns Result.failure(expectedException)

        // When
        val result = useCase.deleteUserAssignedToProject("1", "user1")

        // Then
        assertTrue(result.isFailure)
        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
    }
}