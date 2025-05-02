package data.repo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.User
import org.example.data.datasources.PlanMateDataSource
import org.example.data.repo.UserRepositoryImp
import org.example.logic.model.exceptions.UsersDataAreEmpty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.IOException

class UserRepositoryImpTest {

 private lateinit var userRepository: UserRepositoryImp
 private lateinit var mockDataSource: PlanMateDataSource<User>

 @BeforeEach
 fun setUp() {
  mockDataSource = mockk()
  userRepository = UserRepositoryImp(mockDataSource)
 }

 @Test
 fun `addUser should return success when datasource writes successfully`() {
  // Given
  val user = User("testUser", "hashedPassword")
  every { mockDataSource.append(listOf(user)) } returns Result.success(true)

  // When
  val result = userRepository.createUser(user)

  // Then
  assertTrue(result.isSuccess)
  assertEquals(true, result.getOrNull())
  verify { mockDataSource.append(listOf(user)) }
 }

 @Test
 fun `addUser should return failure when datasource write fails`() {
  // Given
  val user = User("testUser", "hashedPassword")
  val expectedException = IOException("Write failed")
  every { mockDataSource.append(listOf(user)) } returns Result.failure(expectedException)

  // When
  val result = userRepository.createUser(user)

  // Then
  assertTrue(result.isFailure)
  assertEquals(expectedException, result.exceptionOrNull())
  verify { mockDataSource.append(listOf(user)) }
 }

 @Test
 fun `getAllUsers should return success with users when datasource reads successfully`() {
  // Given
  val expectedUsers = listOf(
   User("user1", "hash1"),
   User("user2", "hash2")
  )
  every { mockDataSource.read() } returns Result.success(expectedUsers)

  // When
  val result = userRepository.getAllUsers()

  // Then
  assertTrue(result.isSuccess)
  assertEquals(expectedUsers, result.getOrNull())
  verify { mockDataSource.read() }
 }

 @Test
 fun `getAllUsers should return failure when datasource read fails`() {
  // Given
  val expectedException = UsersDataAreEmpty()
  every { mockDataSource.read() } returns Result.failure(expectedException)

  // When
  val result = userRepository.getAllUsers()

  // Then
  assertTrue(result.isFailure)
  assertEquals(expectedException, result.exceptionOrNull())
  verify { mockDataSource.read() }
 }

 @Test
 fun `getAllUsers should return empty list when datasource returns empty list`() {
  // Given
  every { mockDataSource.read() } returns Result.success(emptyList())

  // When
  val result = userRepository.getAllUsers()

  // Then
  assertTrue(result.isSuccess)
  assertTrue(result.getOrNull()?.isEmpty() ?: false)
  verify { mockDataSource.read() }
 }

 @Test
 fun `addUser should propagate false when datasource returns false`() {
  // Given
  val user = User("testUser", "hashedPassword")
  every { mockDataSource.append(listOf(user)) } returns Result.success(false)

  // When
  val result = userRepository.createUser(user)

  // Then
  assertTrue(result.isSuccess)
  assertEquals(false, result.getOrNull())
  verify { mockDataSource.append(listOf(user)) }
 }
}