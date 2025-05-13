package data.source.local.csv_reader_writer

import com.google.common.truth.Truth.assertThat
import data.dto.UserDto
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.UserCSVReaderWriter
import org.example.logic.utils.HashingService
import org.junit.jupiter.api.*
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserCSVReaderWriterTest {

    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var userCSVReaderWriter: UserCSVReaderWriter
    private lateinit var hashingService: HashingService

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("user_test", ".csv").toFile()
        testFilePath = tempFile.path
        hashingService = mockk(relaxed = true)

        every { hashingService.hash("admin123") } returns "hashedAdminPassword"
        userCSVReaderWriter = UserCSVReaderWriter(testFilePath, hashingService)
    }

    @AfterEach
    fun tearDown() {
        File(testFilePath).takeIf { it.exists() }?.delete()
    }

    @Nested
    inner class ReadTests {
        @Test
        fun `read should create file and add admin if not exists`() = runTest {
            File(testFilePath).delete()

            val result = userCSVReaderWriter.read()

            assertThat(tempFile.exists()).isTrue()
            assertThat(result).hasSize(1)
            assertThat(result[0].username).isEqualTo("rodina")
            assertThat(result[0].hashedPassword).isEqualTo("hashedAdminPassword")
            assertThat(result[0].role).isEqualTo("ADMIN")

            val content = File(testFilePath).readText()
            assertTrue(content.contains("rodina,hashedAdminPassword,ADMIN"))
        }

        @Test
        fun `read should return empty list when file is empty after creation`() = runTest {
            File(testFilePath).writeText("") // Should trigger admin creation

            val result = userCSVReaderWriter.read()

            assertThat(tempFile.exists()).isTrue()
            assertThat(result).hasSize(1)
            assertThat(result[0].username).isEqualTo("rodina")
        }

        @Test
        fun `read should return users when file contains valid data`() = runTest {
            File(testFilePath).writeText(
                """
                id,username,hashedPassword,role
                user1_id,user1,hashed1,USER
                user2_id,user2,hashed2,ADMIN
                """.trimIndent()
            )

            val result = userCSVReaderWriter.read()

            assertEquals(2, result.size)
            assertEquals("user1", result[0].username)
            assertEquals("hashed1", result[0].hashedPassword)
            assertEquals("USER", result[0].role)
            assertEquals("user2", result[1].username)
            assertEquals("hashed2", result[1].hashedPassword)
            assertEquals("ADMIN", result[1].role)
        }

        @Test
        fun `read should add admin if only header exists`() = runTest {
            File(testFilePath).writeText("id,username,hashedPassword,role")

            val result = userCSVReaderWriter.read()

            assertThat(result).hasSize(1)
            assertThat(result[0].username).isEqualTo("rodina")

            val content = File(testFilePath).readText()
            assertTrue(content.contains("rodina,hashedAdminPassword,ADMIN"))
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `overWrite should create file with correct content`() = runTest {
            val users = listOf(
                UserDto("user1_id", "user1", "hashed1", "USER"),
                UserDto("user2_id", "user2", "hashed2", "ADMIN")
            )

            val result = userCSVReaderWriter.overWrite(users)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,hashed1,USER"))
            assertTrue(content.contains("user2,hashed2,ADMIN"))
        }

        @Test
        fun `overWrite should handle empty list`() = runTest {
            val result = userCSVReaderWriter.overWrite(emptyList())
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("id,username,hashedPassword,role"))
            assertThat(content.lines().size).isEqualTo(2)
        }
    }

    @Nested
    inner class AppendTests {
        @Test
        fun `append should add records to existing file`() = runTest {
            val initialUsers = listOf(
                UserDto("user1_id", "user1", "hashed1", "USER")
            )
            userCSVReaderWriter.overWrite(initialUsers)

            val newUsers = listOf(
                UserDto("user2_id", "user2", "hashed2", "ADMIN")
            )

            val result = userCSVReaderWriter.append(newUsers)
            assertTrue(result)

            val content = File(testFilePath).readText()
            assertTrue(content.contains("user1,hashed1,USER"))
            assertTrue(content.contains("user2,hashed2,ADMIN"))
        }

        @Test
        fun `append should create file and add admin if not exists when appending to non-existent file`() = runTest {
            tempFile.delete()

            val newUsers = listOf(
                UserDto("user2_id", "user2", "hashed2", "ADMIN")
            )

            val result = userCSVReaderWriter.append(newUsers)
            assertTrue(result)
            assertTrue(tempFile.exists())

            val content = tempFile.readText()
            assertTrue(content.contains("rodina,hashedAdminPassword,ADMIN"))
            assertTrue(content.contains("user2,hashed2,ADMIN"))
        }

        @Test
        fun `append should handle empty list`() = runTest {
            userCSVReaderWriter.overWrite(listOf(UserDto("test_id", "test", "hash", "USER")))
            val initialContent = File(testFilePath).readText()

            val result = userCSVReaderWriter.append(emptyList())
            assertTrue(result)

            val finalContent = File(testFilePath).readText()
            assertEquals(initialContent, finalContent)
        }
    }
}