package data.repo

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.repo.EntityStateRepositoryImp
import org.example.data.source.EntityStateDataSource
import org.example.logic.repository.EntityStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import org.example.logic.*
import org.junit.jupiter.api.assertThrows

class EntityStateRepositoryImpTest {
    private lateinit var entityStateDataSource: EntityStateDataSource
    private lateinit var entityStateRepository: EntityStateRepository

    @BeforeEach
    fun setUp() {
        entityStateDataSource = mockk(relaxed = true)
        entityStateRepository = EntityStateRepositoryImp(entityStateDataSource)
    }

    @Test
    fun `addEntityState() should return true when added successfully`() = runTest {
        //Given
        coEvery { entityStateDataSource.addEntityState(entityStateDto) } returns true
        //When
        val result = entityStateRepository.addEntityState(entityState)
        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addEntityState() should return false when added failed`() = runTest {
        //Given
        coEvery { entityStateDataSource.addEntityState(entityStateDto) } returns false
        //When
        val result = entityStateRepository.addEntityState(entityState)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addEntityState() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.addEntityState(entityStateDto) } throws Exception()
        assertThrows<EntityStateNotAddedException> {
            entityStateRepository.addEntityState(entityState)
        }
    }
    @Test
    fun `updateEntityState() should return true when updated successfully`() = runTest {
        coEvery { entityStateDataSource.updateEntityState(entityStateDto) } returns true
        val result = entityStateRepository.updateEntityState(entityState)
        assertThat(result).isTrue()
    }

    @Test
    fun `updateEntityState() should return false when update failed`() = runTest {
        coEvery { entityStateDataSource.updateEntityState(entityStateDto) } returns false
        val result = entityStateRepository.updateEntityState(entityState)
        assertThat(result).isFalse()
    }

    @Test
    fun `updateEntityState() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.updateEntityState(entityStateDto) } throws Exception()
        assertThrows<EntityStateNotUpdatedException> {
            entityStateRepository.updateEntityState(entityState)
        }
    }
    @Test
    fun `deleteEntityState() should return true when deleted successfully`() = runTest {
        coEvery { entityStateDataSource.deleteEntityState(entityStateDto) } returns true
        val result = entityStateRepository.deleteEntityState(entityState)
        assertThat(result).isTrue()
    }

    @Test
    fun `deleteEntityState() should return false when delete failed`() = runTest {
        coEvery { entityStateDataSource.deleteEntityState(entityStateDto) } returns false
        val result = entityStateRepository.deleteEntityState(entityState)
        assertThat(result).isFalse()
    }

    @Test
    fun `deleteEntityState() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.deleteEntityState(entityStateDto) } throws Exception()
        assertThrows<EntityStateNotDeletedException> {
            entityStateRepository.deleteEntityState(entityState)
        }
    }
    @Test
    fun `isEntityStateExist() should return true when state exists`() = runTest {
        coEvery { entityStateDataSource.isEntityStateExist("Todo") } returns true
        val result = entityStateRepository.isEntityStateExist("Todo")
        assertThat(result).isTrue()
    }

    @Test
    fun `isEntityStateExist() should return false when state does not exist`() = runTest {
        coEvery { entityStateDataSource.isEntityStateExist("ssss") } returns false
        val result = entityStateRepository.isEntityStateExist("ssss")
        assertThat(result).isFalse()
    }

    @Test
    fun `isEntityStateExist() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.isEntityStateExist("Todo") } throws Exception()
        assertThrows<NoEntityStateFoundException> {
            entityStateRepository.isEntityStateExist("Todo")
        }
    }
    @Test
    fun `getAllEntityStates() should return list when successful`() = runTest {
        coEvery { entityStateDataSource.getAllEntityStates() } returns listOf(entityStateDto)
        val result = entityStateRepository.getAllEntityStates()
        assertThat(result).containsExactly(entityState)
    }

    @Test
    fun `getAllEntityStates() should return empty list when no states exist`() = runTest {
        coEvery { entityStateDataSource.getAllEntityStates() } returns emptyList()
        val result = entityStateRepository.getAllEntityStates()
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAllEntityStates() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.getAllEntityStates() } throws Exception()
        assertThrows<NoEntityStatesFoundedException> {
            entityStateRepository.getAllEntityStates()
        }
    }
    @Test
    fun `getEntityStateByName() should return state when found`() = runTest {
        coEvery { entityStateDataSource.getEntityStateByName("Todo") } returns entityStateDto
        val result = entityStateRepository.getEntityStateByName("Todo")
        assertThat(result).isEqualTo(entityState)
    }

    @Test
    fun `getEntityStateByName() should throw exception when null returned`() = runTest {
        coEvery { entityStateDataSource.getEntityStateByName("Todo") } returns null
        assertThrows<NoEntityStateFoundException> {
            entityStateRepository.getEntityStateByName("Todo")
        }
    }

    @Test
    fun `getEntityStateByName() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.getEntityStateByName("Todo") } throws Exception()
        assertThrows<NoEntityStateFoundException> {
            entityStateRepository.getEntityStateByName("Todo")
        }
    }
    @Test
    fun `getEntityStateByID() should return state when found`() = runTest {
        coEvery { entityStateDataSource.getEntityStateById(entityState.id.toString()) } returns entityStateDto
        val result = entityStateRepository.getEntityStateByID(entityState.id)
        assertThat(result).isEqualTo(entityState)
    }

    @Test
    fun `getEntityStateByID() should throw exception when null returned`() = runTest {
        coEvery { entityStateDataSource.getEntityStateById(entityState.id.toString()) } returns null
        assertThrows<NoEntityStateFoundException> {
            entityStateRepository.getEntityStateByID(entityState.id)
        }
    }

    @Test
    fun `getEntityStateByID() should throw exception on failure`() = runTest {
        coEvery { entityStateDataSource.getEntityStateById(entityState.id.toString()) } throws Exception()
        assertThrows<NoEntityStateFoundException> {
            entityStateRepository.getEntityStateByID(entityState.id)
        }
    }
}
