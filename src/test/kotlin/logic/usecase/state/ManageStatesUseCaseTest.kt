package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.NoEntityStateFoundException
import org.example.logic.NotAllowedEntityStateNameException
import org.example.logic.entities.EntityState
import org.example.logic.repository.EntityStateRepository
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ManageStatesUseCaseTest {

    private lateinit var entityStateRepository: EntityStateRepository
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase

    @BeforeEach
    fun setUp() {
        entityStateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageEntityStatesUseCase(entityStateRepository)
    }

    @Test
    fun `editEntityState()  should return true  when the state name is valid and repo returned true`() =
        runTest {
            //Given
            val stateName = "do"
            val newStateName = "new"
            coEvery { entityStateRepository.getAllEntityStates() } returns
                    listOf(
                        EntityState(
                            UUID.randomUUID(),
                            stateName
                        )
                    )

            coEvery { entityStateRepository.editEntityState(any()) } returns true

            //When
            val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

            //Then
            assertThat(result).isEqualTo(true)
        }

    @Test
    fun `editEntityState()  should return failure with not allowed state name exception when the name of state contain special characters`() =
        runTest {
            //Given
            val stateName = "#In Review$"
            val newStateName = "New ToDo"

            // When & Then
            assertThrows<NotAllowedEntityStateNameException> {
                manageStatesUseCase.editEntityStateByName(stateName, newStateName)
            }
        }

    @Test
    fun `editEntityState() should return failure with exception when edit state fail`() = runTest {
        //Given
        val stateName = "In Review"
        val newStateName = "New ToDo"

        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
        coEvery { entityStateRepository.editEntityState(any()) } throws Throwable()

        // When & Then
        assertThrows<Throwable> {
            manageStatesUseCase.editEntityStateByName(stateName, newStateName)
        }
    }

    @Test
    fun `editEntityState() should return failure with exception when edit states fails with file not found`() =
        runTest {
            //Given
            val stateName = "In Review"
            val newStateName = "New ToDo"

            coEvery { entityStateRepository.editEntityState(any()) } throws Throwable()

            // When & Then
            assertThrows<Throwable> {
                manageStatesUseCase.editEntityStateByName(stateName, newStateName)
            }
        }

    @Test
    fun `editEntityState()  should return failure with not allowed state name exception when the name of state contain number`() =
        runTest {
            //Given
            val stateName = "1In Rev3ew"
            val newStateName = "New ToDo"

            coEvery { entityStateRepository.editEntityState(any()) } throws NotAllowedEntityStateNameException()

            // When & Then
            assertThrows<NotAllowedEntityStateNameException> {
                manageStatesUseCase.editEntityStateByName(stateName, newStateName)
            }
        }

    @Test
    fun `editEntityState()  should return failure with not allowed state name exception when the name is blank string`() =
        runTest {
            //Given
            val stateName = "   "
            val newStateName = "New ToDo"
            coEvery { entityStateRepository.editEntityState(any()) } throws NotAllowedEntityStateNameException()
            // When & Then
            assertThrows<NotAllowedEntityStateNameException> {
                manageStatesUseCase.editEntityStateByName(
                    stateName,
                    newStateName
                )
            }
        }

    @Test
    fun `editEntityState()  should return failure with state not exist exception when state is not exist`() =
        runTest {
            //Given
            val stateName = "TODO"
            val newStateName = "New ToDo"

            coEvery { entityStateRepository.editEntityState(any()) } throws NoEntityStateFoundException()

            // When & Then
            assertThrows<NoEntityStateFoundException> {
                manageStatesUseCase.editEntityStateByName(stateName, newStateName)
            }
        }

    @Test
    fun `deleteEntityState()  should return true with true when the state name exist and the repo added successfully`() =
        runTest {
            //Given
            val stateName = "TODO"
            "New ToDo"

            coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
            coEvery { entityStateRepository.deleteEntityState(any()) } returns true

            //  When
            val result = manageStatesUseCase.deleteEntityState(stateName)

            //Then
            assertThat(result).isEqualTo(true)
        }

    @Test
    fun `deleteEntityState()  should return failure with exception when the state name not valid`() = runTest {
        //Given
        val stateName = "TOD&%O"
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
        coEvery { entityStateRepository.deleteEntityState(any()) } returns true

        // When & Then
        assertThrows<Throwable> { manageStatesUseCase.deleteEntityState(stateName) }
    }

    @Test
    fun `deleteEntityState()  should return failure with exception when repo returns fail with file not found exception`() =
        runTest {
            //Given
            val stateName = "TOD&%O"
            coEvery { entityStateRepository.getAllEntityStates() } throws Throwable()
            // When & Then
            assertThrows<Throwable> {
                manageStatesUseCase.deleteEntityState(stateName)
            }
        }

    @Test
    fun `deleteEntityState()  should return failure with throwable when the state name not exist`() = runTest {
        //Given
        val stateName = "TODO"
        coEvery { entityStateRepository.getEntityStateByName(any()) } throws NotAllowedEntityStateNameException()

        // When & Then
        assertThrows<NotAllowedEntityStateNameException> { manageStatesUseCase.deleteEntityState(stateName) }
    }

    @Test
    fun `deleteEntityState()  should return failure with throwable when repo returned failure while editing`() =
        runTest {
            //Given
            val stateName = "TODO"
            coEvery { entityStateRepository.deleteEntityState(any()) } throws Throwable()

            // When & Then
            assertThrows<Throwable> { manageStatesUseCase.deleteEntityState(stateName) }
        }

    @Test
    fun `addEntityState() should return true with true when the name of state is valid`() = runTest {
        // Given
        val entityState = EntityState(
            id = UUID.randomUUID(),
            name = "doing"
        )

        coEvery { entityStateRepository.isEntityStateExist(entityState.name) } returns false

        coEvery { entityStateRepository.addEntityState(any()) } returns true

        // When
        val result = manageStatesUseCase.addEntityState(entityState.name)

        // Then
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `addEntityState() should return failure with throwable when the addState fails`() = runTest {
        // Given
        val stateName = "ToDo"
        coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()

        // When & Then
        assertThrows<Throwable> { manageStatesUseCase.addEntityState(stateName) }
    }

    @Test
    fun `addEntityState() should return true with true when the name of state have leading and trailing space`() =
        runTest {
            //Given
            val stateName = "   ToDo    "
            coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = "Doing"))
            coEvery { entityStateRepository.addEntityState(any()) } returns true
            //When
            val result = manageStatesUseCase.addEntityState(stateName)

            //Then
            assertThat(result).isEqualTo(true)
        }

    @Test
    fun `addEntityState() should return failure when the state exist`() = runTest {
        //Given
        val stateName = "ToDo"
        coEvery { entityStateRepository.isEntityStateExist(stateName) } returns true
        // When & Then
        assertThrows<Throwable> { manageStatesUseCase.addEntityState(stateName) }
    }
    @Test
    fun `addEntityState() should return failure when the state not  exist`() = runTest {
        //Given
        val stateName = "ToDo"
        coEvery { entityStateRepository.isEntityStateExist(stateName) } throws Throwable()
        // When & Then
        assertThrows<Throwable> { manageStatesUseCase.addEntityState(stateName) }
    }


    @Test
    fun `addEntityState() should return failure with not allowed state name exception when the name of state contain special characters`() =
        runTest {
            //Given
            val stateName = "#I Review$"
            coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()

            // When & Then
            assertThrows<Throwable> { manageStatesUseCase.addEntityState(stateName) }
        }

    @Test
    fun `addEntityState() should return failure with not allowed state name exception when the name of state contain number`() =
        runTest {
            //Given
            val stateName = "1I Rev3ew"
            coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()

            // When & Then
            assertThrows<NotAllowedEntityStateNameException> { manageStatesUseCase.addEntityState(stateName) }
        }

    @Test
    fun `addEntityState() should return failure with not allowed state name exception when the name is blank string`() =
        runTest {
            //Given
            val stateName = "   "
            coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()

            // When & Then
            assertThrows<Throwable> { manageStatesUseCase.addEntityState(stateName) }
        }

    @Test


    fun `addEntityState() should return failure with not allowed length exception when the name of state is more than 30`() =
        runTest {
            //Given
            val state = "hi in this state this is too long state"
            coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()
            // When & Then
            assertThrows<Throwable> {
                manageStatesUseCase.addEntityState(state)
            }
        }

    @Test

    fun `getAllEntityStates() should return true with list of state when the file have data`() = runTest {
        //Given
        val state = listOf(
            EntityState(name = "todo")
        )
        coEvery { entityStateRepository.getAllEntityStates() } returns state
        //  When
        val result = manageStatesUseCase.getAllEntityStates()

        //Then
        assertThat(result).isEqualTo(state)
    }

    @Test
    fun `getAllEntityStates() should return failure with empty data exception when have no data`() = runTest {
        //Given & When
        coEvery { entityStateRepository.getAllEntityStates() } throws Throwable()
        //  When


        //Then
        assertThrows<Throwable> { manageStatesUseCase.getAllEntityStates() }
    }

    @Test
    fun `getAllEntityStates() should return failure with read exception when error happens while reading from data`() =
        runTest {
            //Given
            coEvery { entityStateRepository.getAllEntityStates() } throws Throwable()

            // When & Then
            assertThrows<Throwable> { manageStatesUseCase.getAllEntityStates() }
        }

    @Test
    fun `getEntityStateNameByStateId() should return name  of state when state exist`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getEntityStateByID(any()) } returns projectState

        //  When
        val result = manageStatesUseCase.getEntityStateNameByStateId(projectState.id)

        //Then
        assertThat(result).isEqualTo(projectState.name)
    }

    @Test
    fun `getEntityStateNameByStateId() should return failure  of state when state not exist`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getEntityStateByID(any()) } throws Throwable()

        //  When & Then
        assertThrows<Throwable> { manageStatesUseCase.getEntityStateNameByStateId(projectState.id) }
    }

    @Test
    fun `getEntityStateIdByName() should return failure when state not exist`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getEntityStateByName(any()) } throws Throwable()

        //  When & Then
        assertThrows<Throwable> { manageStatesUseCase.getEntityStateIdByName(projectState.name) }
    }


    @Test
    fun `getEntityStateIdByName() should return id when state name valid`() = runTest {
        // Given
        val projectState = EntityState(id = UUID.fromString("08585840-5cef-4cf2-a71f-4eccf5d7bcb8"), name = "TODO")
        coEvery { entityStateRepository.getEntityStateByName(projectState.name) } returns projectState

        // When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        // Then
        assertThat(result).isEqualTo(projectState.id)
    }

    @Test
    fun `getEntityStateIdByName() should return failure when state name not valid`() = runTest {
        //Given
        val projectState = EntityState(name = "T&^^ODO")
        coEvery { entityStateRepository.getEntityStateByName(any()) } throws Throwable()

        //  When & Then
        assertThrows<Throwable> { manageStatesUseCase.getEntityStateIdByName(projectState.name) }
    }


    @Test
    fun `getEntityStateIdByName() should return failure when getAllEntityStates return failure`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getEntityStateByName(any()) } throws Throwable()

        //  When & Then
        assertThrows<Throwable> { manageStatesUseCase.getEntityStateIdByName(projectState.name) }
    }

}
