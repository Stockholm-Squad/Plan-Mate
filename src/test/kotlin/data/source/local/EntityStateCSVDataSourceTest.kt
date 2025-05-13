package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.EntityStateDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.EntityStateDataSource
import org.example.data.source.local.EntityStateCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EntityStateCSVDataSourceTest {

    private lateinit var readerWriter: IReaderWriter<EntityStateDto>
    private lateinit var dataSource: EntityStateDataSource

    private val sampleState1 = EntityStateDto(id = "1", name = "Active")
    private val sampleState2 = EntityStateDto(id = "2", name = "Inactive")

    @BeforeEach
    fun setup() {
        readerWriter = mockk(relaxed = true)
        dataSource = EntityStateCSVDataSource(readerWriter)
    }

    @Test
    fun `addEntityState should append to CSV and return true when state added`() = runTest {
        //Given
        coEvery { readerWriter.append(listOf(sampleState1)) } returns true

        //When
        val result = dataSource.addEntityState(sampleState1)

        //Then
        assertThat(result).isTrue()
        coVerify { readerWriter.append(listOf(sampleState1)) }
    }

    @Test
    fun `addEntityState should return false when append fails`() = runTest {
        //Given
        coEvery { readerWriter.append(listOf(sampleState1)) } returns false

        //When
        val result = dataSource.addEntityState(sampleState1)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `updateEntityState should overwrite with updated state when state updated`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1, sampleState2)
        coEvery { readerWriter.overWrite(any()) } returns true

        val updated = sampleState1.copy(name = "Updated")

        //When
        val result = dataSource.updateEntityState(updated)

        //Then
        assertThat(result).isTrue()
        coVerify {
            readerWriter.overWrite(
                match { list ->
                    list.any { it.id == "1" && it.name == "Updated" } &&
                            list.any { it.id == "2" && it.name == "Inactive" }
                }
            )
        }
    }

    @Test
    fun `updateEntityState should return false if overwrite fails`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1)
        coEvery { readerWriter.overWrite(any()) } returns false

        //When
        val result = dataSource.updateEntityState(sampleState1.copy(name = "Changed"))

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `updateEntityState should overwrite unchanged list when no state found`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState2)
        coEvery { readerWriter.overWrite(listOf(sampleState2)) } returns true

        val updated = sampleState1.copy(name = "Updated")

        //When
        val result = dataSource.updateEntityState(updated)

        //Then
        assertThat(result).isTrue()
        coVerify {
            readerWriter.overWrite(
                match { list -> list == listOf(sampleState2) }
            )
        }
    }

    @Test
    fun `deleteEntityState should remove the matching entity by id when delete called`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1, sampleState2)
        coEvery { readerWriter.overWrite(any()) } returns true

        //When
        val result = dataSource.deleteEntityState(sampleState1)

        //Then
        assertThat(result).isTrue()
        coVerify {
            readerWriter.overWrite(
                match { list -> list.size == 1 && list[0].id == "2" }
            )
        }
    }

    @Test
    fun `deleteEntityState should return false if overwrite fails`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1, sampleState2)
        coEvery { readerWriter.overWrite(any()) } returns false

        //When
        val result = dataSource.deleteEntityState(sampleState1)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteEntityState should not change list when id not found`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState2)
        coEvery { readerWriter.overWrite(any()) } returns true

        //When
        val result = dataSource.deleteEntityState(sampleState1)

        //Then
        assertThat(result).isTrue()
        coVerify {
            readerWriter.overWrite(
                match { list -> list == listOf(sampleState2) }
            )
        }
    }

    @Test
    fun `deleteEntityState should do nothing when list is empty`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns emptyList()
        coEvery { readerWriter.overWrite(any()) } returns true

        //When
        val result = dataSource.deleteEntityState(sampleState1)

        //Then
        assertThat(result).isTrue()
        coVerify { readerWriter.overWrite(emptyList()) }
    }

    @Test
    fun `isEntityStateExist should return true when state with id exists`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1)

        //When
        val result = dataSource.isEntityStateExist("1")

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isEntityStateExist should return false when state with id does not exist`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1)

        //When
        val result = dataSource.isEntityStateExist("nonexistent")

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `getAllEntityStates should return list of states when called`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1, sampleState2)

        //When
        val result = dataSource.getAllEntityStates()

        //Then
        assertThat(result).containsExactly(sampleState1, sampleState2)
    }

    @Test
    fun `getEntityStateByName should return the correct state when state found`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1, sampleState2)

        //When
        val result = dataSource.getEntityStateByName("Inactive")

        //Then
        assertThat(result).isEqualTo(sampleState2)
    }

    @Test
    fun `getEntityStateByName should return null when state name not found`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1)

        //When
        val result = dataSource.getEntityStateByName("Nonexistent")

        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getEntityStateByName should return null when no states`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns emptyList()

        //When
        val result = dataSource.getEntityStateByName("Nonexistent")

        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getEntityStateById should return correct entity when id exist`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1)

        //When
        val result = dataSource.getEntityStateById("1")

        //Then
        assertThat(result).isEqualTo(sampleState1)
    }

    @Test
    fun `getEntityStateById should return null when entity not found`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleState1)

        //When
        val result = dataSource.getEntityStateById("nonexistent")

        //Then
        assertThat(result).isNull()
    }
}