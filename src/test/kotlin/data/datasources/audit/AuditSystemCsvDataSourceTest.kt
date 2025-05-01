import com.google.common.truth.Truth.assertThat
import org.example.data.datasources.AuditSystemCsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
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
    fun `read should return failure when file does not exist`() {
        //given
        val nonExistentFile = File(tempFile.parent, "nonexistent.csv")
        val ds = AuditSystemCsvDataSource(nonExistentFile.absolutePath)
        //when
        val result = ds.read()
        //then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.DataException.FileNotExistException::class.java)
    }

    @Test
    fun `read should return success`() {
        //given
        val ds = AuditSystemCsvDataSource("audits.csv")
        //when
        val result = ds.read()
        //then
        assertThat(result.isSuccess).isTrue()
    }



    @Test
    fun `read should return failure when CSV is malformed`() {
        //given
        tempFile.writeText("this,is,not,valid,data\ninvalid,line")
        //when
        val result = dataSource.read()
        //then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(PlanMateExceptions.DataException.ReadException::class.java)
    }
}
