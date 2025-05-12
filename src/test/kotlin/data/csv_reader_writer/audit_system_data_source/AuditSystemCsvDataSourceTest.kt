package data.csv_reader_writer.audit_system_data_source

import com.google.common.truth.Truth.assertThat
import data.dto.AuditDto
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv_reader_writer.AuditCSVReaderWriter
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class AuditSystemCsvDataSourceTest {

    private lateinit var dataSource: IReaderWriter<AuditDto>
    private lateinit var tempFile: File

    @BeforeEach
    fun setUp() {
        tempFile = File.createTempFile("audit_test", ".csv")
        tempFile.deleteOnExit()
        dataSource = AuditCSVReaderWriter(tempFile.absolutePath)
    }

    @AfterEach
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun `read should create and return empty list if file doesn't exist`() = runTest {
        val nonExistentFile = File(tempFile.parent, "nonexistent.csv")
        val ds = AuditCSVReaderWriter(nonExistentFile.absolutePath)

        val result = ds.read()

        assertThat(result).isEmpty()
        assertThat(nonExistentFile.exists()).isTrue()
    }

    @Test
    fun `read should return failure if file creation throws exception`() = runTest {
        val directory = File(tempFile.parentFile, "fakeDir")
        directory.mkdir()
        val invalidFile = File(directory, "another/fake.csv")

        val ds = AuditCSVReaderWriter(invalidFile.absolutePath)

        assertThrows<Throwable> { ds.read() }
        directory.delete()
    }

    @Test
    fun `read should return empty list if file has less than 2 lines`() = runTest {
        tempFile.writeText("only,header,line\n")
        val result = dataSource.read()

        assertThat(result).isEmpty()
    }

    @Test
    fun `read should return failure when CSV is malformed`() = runTest {
        tempFile.writeText("bad,data,line\n1,2,3")

        assertThrows<Throwable> { dataSource.read() }
    }

    @Test
    fun `overWrite should write data and return success`() = runTest {
        val models = listOf(
            AuditDto("u1", "Add", "Success", "Admin", "test", "test"),
            AuditDto("u2", "Delete", "Failed", "User", "2023-01-02", "test")
        )

        val result = dataSource.overWrite(models)

        assertThat(result).isTrue()
        assertThat(tempFile.readText()).contains("u1")
    }

    @Test
    fun `overWrite should return failure on write exception`() = runTest {
        val invalidDataSource = AuditCSVReaderWriter("/invalid/path/to/file.csv")

        assertThrows<Throwable> { invalidDataSource.overWrite(emptyList()) }
    }

    @Test
    fun `append should write data to empty file`() = runTest {
        val models = listOf(
            AuditDto("u1", "Update", "Success", "Admin", "test", "test")
        )

        val result = dataSource.append(models)

        assertThat(result).isTrue()
        assertThat(tempFile.readText()).contains("u1")
    }

    @Test
    fun `append should append to existing data`() = runTest {
        val initialData = listOf(
            AuditDto("u1", "Update", "Success", "Admin", "test", "test")
        )
        dataSource.overWrite(initialData)

        val moreData = listOf(
            AuditDto("u2", "Delete", "Failed", "Admin", "test", "test")
        )
        val result = dataSource.append(moreData)

        assertThat(result).isTrue()
        val content = tempFile.readText()
        assertThat(content).contains("u1")
        assertThat(content).contains("u2")
    }

    @Test
    fun `append should return failure on exception`() = runTest {
        val invalidDataSource = AuditCSVReaderWriter("/invalid/path/to/file.csv")

        assertThrows<Throwable> { invalidDataSource.append(emptyList()) }
    }

    @Test
    fun `read should return empty list when creating new file`() = runTest {
        // Ensure file doesn't exist initially
        tempFile.delete()

        val result = dataSource.read()

        assertThat(result).isEmpty()
        assertThat(tempFile.exists()).isTrue()
    }

}
