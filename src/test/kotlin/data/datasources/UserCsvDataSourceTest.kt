package data.datasources

import com.google.common.truth.Truth.assertThat
import org.example.data.models.UserModel
import org.example.data.datasources.user_data_source.UserCsvDataSource
import org.example.logic.model.exceptions.FileNotExistException
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.model.exceptions.ReadDataException
import org.junit.jupiter.api.*
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UserCsvDataSourceTest {
    private lateinit var tempFile: File
    private lateinit var testFilePath: String
    private lateinit var dataSource: UserCsvDataSource

    @BeforeEach
    fun setUp() {
        tempFile = Files.createTempFile("tst", ".csv").toFile()
        testFilePath = tempFile.path
        dataSource = UserCsvDataSource(testFilePath)
    }

    @AfterEach
    fun tearDown() {
        File(testFilePath).takeIf { it.exists() }?.delete()
    }

    @Nested
    inner class ReadTests {
        @Test
        fun `read should fail and return ReadException when file format is invalid`() {
            val invalidCsvContent = """
        hashedPassword,role
        someHash,MATE
    """.trimIndent()
            File(testFilePath).writeText(invalidCsvContent)

            assertThrows<ReadDataException> {dataSource.read().getOrThrow()  }
        }

        @Test
        fun `read should return FileNotExistException when file doesn't exist`() {
            File(testFilePath).delete()

            val result = dataSource.read()

            assertTrue(result.isFailure)
            assertFailsWith<FileNotExistException> {
                result.getOrThrow()
            }
        }

        @Test
        fun `read should return Throwable when invalid path`() {
            dataSource = UserCsvDataSource("")

            val result = dataSource.read()

            assertTrue(result.isFailure)
            assertFailsWith<Throwable> { result.getOrThrow() }
        }

        @Test
        fun `read should return empty list when file is empty`() {
            File(testFilePath).writeText("id,username,hashedPassword,role")

            val result = dataSource.read()

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

        @Test
        fun `read should return empty list when file has only header`() {
            File(testFilePath).writeText("id,username,hashedPassword,role")

            val result = dataSource.read()

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

        @Test
        fun `read should return users when file contains valid data`() {
            File(testFilePath).writeText(
                """
                id,username,hashedPassword,role
               3a3e6a1a-5e9e-4f0c-9b3d-8c1e6f2a7b1c, rodina,5f4dcc3b5aa765d61d8327deb882cf99,MATE
                3a3e6a1a-5e9e-4f0c-9b3d-8c1e6f2a7b1c,admin,e99a18c428cb38d5f260853678922e03,ADMIN
                """.trimIndent()
            )

            val result = dataSource.read()

            assertTrue(result.isSuccess)
            val users = result.getOrThrow()
            assertThat(users).hasSize(2)
            assertThat(users[0]).isEqualTo(UserModel(UUID.randomUUID().toString(),"rodina", "5f4dcc3b5aa765d61d8327deb882cf99", "Role.MATE"))
            assertThat(users[1]).isEqualTo(UserModel(UUID.randomUUID().toString(),"admin", "e99a18c428cb38d5f260853678922e03"," Role.ADMIN"))
        }
    }

    @Nested
    inner class WriteTests {
        @Test
        fun `write should create file with correct content`() {
            val users = listOf(
                UserModel(id = UUID.randomUUID().toString(), username = "rodina", hashedPassword = "123md5hash", role = "Role.MATE"),
                UserModel(id = UUID.randomUUID().toString(), username = "admin", hashedPassword = "adminmd5hash", role = "Role.ADMIN")
            )

            val result = dataSource.append(users)

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            println("File content:\n$content")
            assertTrue(content.contains("123md5hash,MATE,rodina"))
            assertTrue(content.contains("adminmd5hash,ADMIN,admin"))
        }

        @Test
        fun `write should handle empty list`() {
            val result = dataSource.overWrite(emptyList())

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow())

            val content = File(testFilePath).readText()
            // May be empty or may contain header only based on implementation
            assertThat(content.trim()).isEmpty()
        }
    }
}
