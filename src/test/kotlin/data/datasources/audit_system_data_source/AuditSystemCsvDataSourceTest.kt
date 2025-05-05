package data.datasources.audit_system_data_source

import com.google.common.truth.Truth.assertThat
import logic.models.exceptions.FileNotExistException
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.WriteDataException
import org.example.data.datasources.audit_system_data_source.AuditSystemCsvDataSource
import org.example.data.models.AuditSystemModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class AuditSystemCsvDataSourceTest {

    private lateinit var dataSource: AuditSystemCsvDataSource
    private lateinit var tempFile: File

    @BeforeEach
    fun setUp() {
        tempFile = File.createTempFile("audit_test", ".csv")
        tempFile.deleteOnExit()
        dataSource = AuditSystemCsvDataSource(tempFile.absolutePath)
    }

    @AfterEach
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun `read should create and return empty list if file doesn't exist`() {
        val nonExistentFile = File(tempFile.parent, "nonexistent.csv")
        val ds = AuditSystemCsvDataSource(nonExistentFile.absolutePath)

        val result = ds.read()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
        assertThat(nonExistentFile.exists()).isTrue()
    }

    @Test
    fun `read should return failure if file creation throws exception`() {
        val directory = File(tempFile.parentFile, "fakeDir")
        directory.mkdir()
        val invalidFile = File(directory, "another/fake.csv")

        val ds = AuditSystemCsvDataSource(invalidFile.absolutePath)
        val result = ds.read()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(FileNotExistException::class.java)

        directory.delete()
    }

    @Test
    fun `read should return empty list if file has less than 2 lines`() {
        tempFile.writeText("only,header,line\n")
        val result = dataSource.read()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `read should return failure when CSV is malformed`() {
        tempFile.writeText("bad,data,line\n1,2,3")
        val result = dataSource.read()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(ReadDataException::class.java)
    }

    @Test
    fun `overWrite should write data and return success`() {
        val models = listOf(
            AuditSystemModel("u1", "Add", "Success", "Admin", "test", "test"),
            AuditSystemModel("u2", "Delete", "Failed", "User", "2023-01-02", "test")
        )

        val result = dataSource.overWrite(models)

        assertThat(result.isSuccess).isTrue()
        assertThat(tempFile.readText()).contains("u1")
    }

    @Test
    fun `overWrite should return failure on write exception`() {
        val invalidDataSource = AuditSystemCsvDataSource("/invalid/path/to/file.csv")
        val result = invalidDataSource.overWrite(emptyList())

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(WriteDataException::class.java)
    }

    @Test
    fun `append should write data to empty file`() {
        val models = listOf(
            AuditSystemModel("u1", "Update", "Success", "Admin", "test", "test")
        )

        val result = dataSource.append(models)

        assertThat(result.isSuccess).isTrue()
        assertThat(tempFile.readText()).contains("u1")
    }

    @Test
    fun `append should append to existing data`() {
        val initialData = listOf(
            AuditSystemModel("u1", "Update", "Success", "Admin", "test", "test")
        )
        dataSource.overWrite(initialData)

        val moreData = listOf(
            AuditSystemModel("u2", "Delete", "Failed", "Admin", "test", "test")
        )
        val result = dataSource.append(moreData)

        assertThat(result.isSuccess).isTrue()
        val content = tempFile.readText()
        assertThat(content).contains("u1")
        assertThat(content).contains("u2")
    }

    @Test
    fun `append should return failure on exception`() {
        val invalidDataSource = AuditSystemCsvDataSource("/invalid/path/to/file.csv")
        val result = invalidDataSource.append(emptyList())

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(WriteDataException::class.java)
    }
}
