package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.EntityStateAlreadyExistException
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
    fun `editState() should return success result with true when the state name is valid and repo returned success result of true`() =
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

//    @Test
//    fun `editState() should return success result with true when the name of state have leading and trailing space and repo returned success result of true`() = runTest {
//        //Given
//        val stateName = "ToDo"
//        val newStateName="New ToDo"
//        coEvery { projectStateRepository.getAllProjectStates() } returns  listOf(ProjectState(name= "ToDo")))
//        coEvery { manageStatesUseCase.editProjectStateByName(stateName,newStateName) } returns  true)
//
//        //When
//        val result = manageStatesUseCase.editProjectStateByName(stateName,newStateName)
//
//        //Then
//        assertThat(result.getOrNull()).isEqualTo(true)
//    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain special characters`() =
        runTest {
            //Given
            val stateName = "#In Review$"
            val newStateName = "New ToDo"

            //When
            val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test
    fun `editState() should return failure result with exception when edit state failes`() = runTest {
        //Given
        val stateName = "In Review"
        val newStateName = "New ToDo"

        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
        coEvery { entityStateRepository.editEntityState(any()) } throws Throwable ()

        //When
        val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<Throwable> { result }
    }

    @Test
    fun `editState() should return failure result with exception when edit states fails with file not found`() =
        runTest {
            //Given
            val stateName = "In Review"
            val newStateName = "New ToDo"

            coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
            coEvery { entityStateRepository.editEntityState(any()) } throws NotAllowedEntityStateNameException()

            //When
            val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain number`() =
        runTest {
            //Given
            val stateName = "1In Rev3ew"
            val newStateName = "New ToDo"

            coEvery { entityStateRepository.editEntityState(any()) } throws NotAllowedEntityStateNameException()
            //When
            val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name is blank string`() =
        runTest {
            //Given
            val stateName = "   "
            val newStateName = "New ToDo"
            coEvery { entityStateRepository.editEntityState(any()) } throws NotAllowedEntityStateNameException()

            //When
            val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test
    fun `editState() should return failure result with state not exist exception when state is not exist`() = runTest {
        //Given
        val stateName = "TODO"
        val newStateName = "New ToDo"

        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = "tyyyg"))

        //When
        val result = manageStatesUseCase.editEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedEntityStateNameException> { result }
    }

    @Test
    fun `deleteState() should return success result with true when the state name exist and the repo added successfully`() =
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
    fun `deleteState() should return failure result with exception when the state name not valid`() = runTest {
        //Given
        val stateName = "TOD&%O"
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
        coEvery { entityStateRepository.deleteEntityState(any()) } returns true

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThrows<Throwable> { result }
    }

    @Test
    fun `deleteState() should return failure result with exception when repo returns   failed with file not found exception`() =
        runTest {
            //Given
            val stateName = "TOD&%O"
            coEvery { entityStateRepository.getAllEntityStates() } throws Throwable()


            //  When
            val result = manageStatesUseCase.deleteEntityState(stateName)

            //Then
            assertThrows<Throwable> { result }
        }

    @Test
    fun `deleteState() should return failure result with throwable when the state name not exist`() = runTest {
        //Given
        val stateName = "TODO"
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = "In Progress"))

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThrows<NotAllowedEntityStateNameException> { result }
    }

    @Test
    fun `deleteState() should return failure result with throwable when repo returned failure result while editing`() =
        runTest {
            //Given
            val stateName = "TODO"
            coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))
            coEvery { entityStateRepository.deleteEntityState(any()) } throws Throwable()

            //  When
            val result = manageStatesUseCase.deleteEntityState(stateName)

            //Then
            assertThrows<Throwable> { result }
        }

    @Test
    fun `addState() should return success result with true when the name of state is valid`() = runTest {
        // Given
        val entityState = EntityState(
            id = UUID.randomUUID(),
            name = "todo"
        )

        coEvery { entityStateRepository.getAllEntityStates() } returns
                listOf(
                    EntityState(name = "Done"),
                    EntityState(name = "doing")
                )

        coEvery { entityStateRepository.addEntityState(entityState) } returns true

        // When
        val result = manageStatesUseCase.addEntityState(entityState.name)

        // Then
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with throwable when the addState fails`() = runTest {
        // Given
        val stateName = "ToDo"
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = "Done"))
        coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()
        // When 
        val result = manageStatesUseCase.addEntityState(stateName)
        // Then
        assertThrows<Throwable> { result }
    }

    @Test
    fun `addState() should return success result with true when the name of state have leading and trailing space`() =
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
    fun `addState() should return failure result when the state exist`() = runTest {
        //Given
        val stateName = "ToDo"
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(EntityState(name = stateName))

        //When
        val result = manageStatesUseCase.addEntityState(stateName)

        //Then
        assertThrows<EntityStateAlreadyExistException> { result }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain special characters`() =
        runTest {
            //Given
            val stateName = "#I Review$"
            coEvery { entityStateRepository.addEntityState(any()) } throws Throwable()
            //When
            val result = manageStatesUseCase.addEntityState(stateName)
            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain number`() =
        runTest {
            //Given
            val stateName = "1I Rev3ew"
            coEvery { entityStateRepository.addEntityState(any()) } throws NotAllowedEntityStateNameException()
            //When
            val result = manageStatesUseCase.addEntityState(stateName)

            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name is blank string`() =
        runTest {
            //Given
            val stateName = "   "
            coEvery { entityStateRepository.addEntityState(any()) } returns true

            //When
            val result = manageStatesUseCase.addEntityState(stateName)

            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test


    fun `addState() should return failure result with not allowed length exception when the name of state is more than 30`() =
        runTest {
            //Given
            val state = "hi in this state this is too long state"
            coEvery { entityStateRepository.addEntityState(any()) } throws NotAllowedEntityStateNameException()
            //When
            val result = manageStatesUseCase.addEntityState(state)
            //Then
            assertThrows<NotAllowedEntityStateNameException> { result }
        }

    @Test

    fun `getAllStates() should return success result with list of state when the file have data`() = runTest {
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
    fun `getAllStates() should return failure result with empty data exception when have no data`() = runTest {
        //Given & When
        coEvery { entityStateRepository.getAllEntityStates() } returns emptyList()
        //  When
        val result = manageStatesUseCase.getAllEntityStates()

        //Then
        assertThrows<Throwable> { result }
    }

    @Test
    fun `getAllStates() should return failure result with read exception when error happens while reading from data`() =
        runTest {
            //Given
            coEvery { entityStateRepository.getAllEntityStates() } throws Throwable()

            //  When
            val result = manageStatesUseCase.getAllEntityStates()

            //Then
            assertThrows<Throwable> { result }
        }

    @Test
    fun `getStateIdByName() should return id of state when state exist`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(projectState)

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(projectState.id)
    }

    @Test
    fun `getStateIdByName() should return null when state not exist`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf(projectState)

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName("injhb")

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when state name not valid`() = runTest {
        //Given
        val projectState = EntityState(name = "T&^^ODO")
        coEvery { entityStateRepository.getAllEntityStates() } returns listOf()

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when getAllState return failure`() = runTest {
        //Given
        val projectState = EntityState(name = "TODO")
        coEvery { entityStateRepository.getAllEntityStates() } throws Throwable()

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

}
