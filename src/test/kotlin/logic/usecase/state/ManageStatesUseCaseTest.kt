package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import logic.models.exceptions.*
import org.example.logic.entities.EntityState
import org.example.logic.repository.EntityStateRepository
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ManageStatesUseCaseTest {

    private lateinit var projectStateRepository: EntityStateRepository
    private lateinit var manageStatesUseCase: ManageEntityStatesUseCase

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageEntityStatesUseCase(projectStateRepository)
    }

    @Test
    fun `editState() should return success result with true when the state name is valid and repo returned success result of true`() {
        //Given
        val stateName = "do"
        val newStateName = "new"
        every { projectStateRepository.getAllEntityStates() } returns Result.success(
            listOf(
                EntityState(
                    UUID.randomUUID(),
                    stateName
                )
            )
        )
        every { projectStateRepository.updateEntityState(any()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

//    @Test
//    fun `editState() should return success result with true when the name of state have leading and trailing space and repo returned success result of true`() {
//        //Given
//        val stateName = "ToDo"
//        val newStateName="New ToDo"
//        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name= "ToDo")))
//        every { manageStatesUseCase.editProjectStateByName(stateName,newStateName) } returns Result.success(true)
//
//        //When
//        val result = manageStatesUseCase.editProjectStateByName(stateName,newStateName)
//
//        //Then
//        assertThat(result.getOrNull()).isEqualTo(true)
//    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given
        val stateName = "#In Review$"
        val newStateName = "New ToDo"

        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with exception when edit state failes`() {
        //Given
        val stateName = "In Review"
        val newStateName = "New ToDo"

        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = stateName)))
        every { projectStateRepository.updateEntityState(any()) } returns Result.failure(Throwable())

        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with exception when edit states fails with file not found`() {
        //Given
        val stateName = "In Review"
        val newStateName = "New ToDo"

        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = stateName)))
        every { projectStateRepository.updateEntityState(any()) } returns Result.failure(FileNotExistException())

        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val stateName = "1In Rev3ew"
        val newStateName = "New ToDo"

        every { projectStateRepository.updateEntityState(any()) } returns Result.failure(NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val stateName = "   "
        val newStateName = "New ToDo"
        every { projectStateRepository.updateEntityState(any()) } returns Result.failure(NotAllowedStateNameException())

        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with state not exist exception when state is not exist`() {
        //Given
        val stateName = "TODO"
        val newStateName = "New ToDo"

        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = "tyyyg")))

        //When
        val result = manageStatesUseCase.updateEntityStateByName(stateName, newStateName)

        //Then
        assertThrows<StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return success result with true when the state name exist and the repo added successfully`() {
        //Given
        val stateName = "TODO"
        "New ToDo"

        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = stateName)))
        every { projectStateRepository.deleteEntityState(any()) } returns Result.success(true)

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThat(result.getOrThrow()).isEqualTo(true)
    }

    @Test
    fun `deleteState() should return failure result with exception when the state name not valid`() {
        //Given
        val stateName = "TOD&%O"
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = stateName)))
        every { projectStateRepository.deleteEntityState(any()) } returns Result.success(true)

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with exception when repo returns failed with file not found exception`() {
        //Given
        val stateName = "TOD&%O"
        every { projectStateRepository.getAllEntityStates() } returns Result.failure(
            FileNotExistException()
        )

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when the state name not exist`() {
        //Given
        val stateName = "TODO"
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = "In Progress")))

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThrows<StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when repo returned failure result while editing`() {
        //Given
        val stateName = "TODO"
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = stateName)))
        every { projectStateRepository.deleteEntityState(any()) } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.deleteEntityState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the name of state is valid`() {
        // Given
        val stateName = "ToDo"

        every { projectStateRepository.getAllEntityStates() } returns Result.success(
            listOf(
                EntityState(name = "Done"),
                EntityState(name = "doing")
            )
        )
        every { projectStateRepository.addEntityState(stateName) } returns Result.success(true)

        // When
        val result = manageStatesUseCase.addEntityState(stateName)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with throwable when the addState fails`() {
        // Given
        val stateName = "ToDo"
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = "Done")))
        every { projectStateRepository.addEntityState(any()) } returns Result.failure(Throwable())
        // When
        val result = manageStatesUseCase.addEntityState(stateName)
        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the name of state have leading and trailing space`() {
        //Given
        val stateName = "   ToDo    "
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = "Doing")))
        every { projectStateRepository.addEntityState(stateName.trim()) } returns Result.success(true)
        //When
        val result = manageStatesUseCase.addEntityState(stateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result when the state exist`() {
        //Given
        val stateName = "ToDo"
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(EntityState(name = stateName)))

        //When
        val result = manageStatesUseCase.addEntityState(stateName)

        //Then
        assertThrows<StateAlreadyExistException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given
        val stateName = "#I Review$"
        every { projectStateRepository.addEntityState(any()) } returns Result.failure(Throwable())
        //When
        val result = manageStatesUseCase.addEntityState(stateName)
        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val stateName = "1I Rev3ew"
        every { projectStateRepository.addEntityState(stateName) } returns Result.failure(NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.addEntityState(stateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val stateName = "   "
        every { projectStateRepository.addEntityState(any()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.addEntityState(stateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test


    fun `addState() should return failure result with not allowed length exception when the name of state is more than 30`() {
        //Given
        val state = "hi in this state this is too long state"
        every { projectStateRepository.addEntityState(state) } returns Result.failure(StateNameLengthException())
        //When
        val result = manageStatesUseCase.addEntityState(state)
        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test

    fun `getAllStates() should return success result with list of state when the file have data`() {
        //Given
        val state = listOf(
            EntityState(name = "todo")
        )
        every { projectStateRepository.getAllEntityStates() } returns Result.success(state)
        //  When
        val result = manageStatesUseCase.getAllEntityStates()

        //Then
        assertThat(result.getOrThrow()).isEqualTo(state)
    }

    @Test
    fun `getAllStates() should return failure result with empty data exception when have no data`() {
        //Given & When
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf())
        //  When
        val result = manageStatesUseCase.getAllEntityStates()

        //Then
        assertThrows<EmptyDataException> { result.getOrThrow() }
    }

    @Test
    fun `getAllStates() should return failure result with read exception when error happens while reading from data`() {
        //Given
        every { projectStateRepository.getAllEntityStates() } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.getAllEntityStates()

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `getStateIdByName() should return id of state when state exist`() {
        //Given
        val projectState = EntityState(name = "TODO")
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(projectState))

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(projectState.id)
    }

    @Test
    fun `getStateIdByName() should return null when state not exist`() {
        //Given
        val projectState = EntityState(name = "TODO")
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf(projectState))

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName("injhb")

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when state name not valid`() {
        //Given
        val projectState = EntityState(name = "T&^^ODO")
        every { projectStateRepository.getAllEntityStates() } returns Result.success(listOf())

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when getAllState return failure`() {
        //Given
        val projectState = EntityState(name = "TODO")
        every { projectStateRepository.getAllEntityStates() } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.getEntityStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

}
