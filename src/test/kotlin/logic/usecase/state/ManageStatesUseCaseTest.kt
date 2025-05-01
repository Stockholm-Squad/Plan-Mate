package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import createState
import io.mockk.every
import io.mockk.mockk
import logic.model.entities.State
import org.example.logic.model.exceptions.PlanMateExceptions.DataException
import org.example.logic.model.exceptions.PlanMateExceptions.LogicException
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ManageStatesUseCaseTest {

    private lateinit var stateRepository: StateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase

    @BeforeEach
    fun setUp() {
        stateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(stateRepository)
    }

    @Test
    fun `editState() should return success result with true when the state name is valid and repo returned success result of true`() {
        //Given
        val stateName = "do"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("43345", stateName)))
        every { stateRepository.editState(any()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return success result with true when the name of state have leading and trailing space and repo returned success result of true`() {
        //Given
        val stateName = "    ToDo    "
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("43345", "ToDo")))
        every { stateRepository.editState(any()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given
        val stateName = "#In Review$"

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with exception when edit state failes`() {
        //Given
        val stateName = "In Review"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("567", stateName)))
        every { stateRepository.editState(any()) } returns Result.failure(Throwable())

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with exception when edit states fails with file not found`() {
        //Given
        val stateName = "In Review"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("567", stateName)))
        every { stateRepository.editState(any()) } returns Result.failure(DataException.FileNotExistException())

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThrows<LogicException.StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val stateName = "1In Rev3ew"
        every { stateRepository.editState(any()) } returns Result.failure(LogicException.NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val stateName = ""
        every { stateRepository.editState(any()) } returns Result.failure(LogicException.NotAllowedStateNameException())

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with state not exist exception when state is not exist`() {
        //Given
        val stateName = "TODO"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("6545", "tyyyg")))

        //When
        val result = manageStatesUseCase.editState(stateName)

        //Then
        assertThrows<LogicException.StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return success result with true when the state name exist and the repo added successfully`() {
        //Given
        val stateName = "TODO"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("245", stateName)))
        every { stateRepository.deleteState(any()) } returns Result.success(true)

        //  When
        val result = manageStatesUseCase.deleteState(stateName)

        //Then
        assertThat(result.getOrThrow()).isEqualTo(true)
    }

    @Test
    fun `deleteState() should return failure result with exception when the state name not valid`() {
        //Given
        val stateName = "TOD&%O"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("245", stateName)))
        every { stateRepository.deleteState(any()) } returns Result.success(true)

        //  When
        val result = manageStatesUseCase.deleteState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with exception when repo returns failed with file not found exception`() {
        //Given
        val stateName = "TOD&%O"
        every { stateRepository.getAllStates() } returns Result.failure(
            DataException.FileNotExistException()
        )

        //  When
        val result = manageStatesUseCase.deleteState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when the state name not exist`() {
        //Given
        val stateName = "TODO"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("245", "In Progress")))

        //  When
        val result = manageStatesUseCase.deleteState(stateName)

        //Then
        assertThrows<LogicException.StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when repo returned failure result while editing`() {
        //Given
        val stateName = "TODO"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("245", stateName)))
        every { stateRepository.deleteState(any()) } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.deleteState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the name of state is valid`() {
        // Given
        val stateName = "ToDo"
        val existingStates = listOf(State(name = "Done"))

        every { stateRepository.getAllStates() } returns Result.success(existingStates)
        every { stateRepository.addState(any()) } returns Result.success(true)

        // When
        val result = manageStatesUseCase.addState(stateName)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with throwable when the addState fails`() {
        // Given
        val stateName = "ToDo"
        val existingStates = listOf(State(name = "Done"))

        every { stateRepository.getAllStates() } returns Result.success(existingStates)
        every { stateRepository.addState(any()) } returns Result.failure(Throwable())

        // When
        val result = manageStatesUseCase.addState(stateName)

        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the name of state have leading and trailing space`() {
        //Given
        val stateName = "   ToDo    "
        val existingStates = listOf(State(name = "Done"))
        every { stateRepository.getAllStates() } returns Result.success(existingStates)
        every { stateRepository.addState(stateName.trim()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.addState(stateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result when the state exist`() {
        //Given
        val stateName = "ToDo"
        every { stateRepository.getAllStates() } returns Result.success(listOf(State("344", stateName)))

        //When
        val result = manageStatesUseCase.addState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given
        val stateName = "#I Review$"
        every { stateRepository.addState(stateName) } returns Result.failure(LogicException.NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.addState(stateName)
        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val stateName = "1I Rev3ew"
        every { stateRepository.addState(stateName) } returns Result.failure(LogicException.NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.addState(stateName)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val stateName = ""
        every { stateRepository.addState(stateName) } returns Result.failure(LogicException.NotAllowedStateNameException())

        //When
        val result = manageStatesUseCase.addState(stateName)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test


    fun `addState() should return failure result with not allowed length exception when the name of state is more than 30`() {
        //Given
        val state = "hi in this state this is too long state"
        every { stateRepository.addState(state) } returns Result.failure(LogicException.StateNameLengthException())
        //When
        val result = manageStatesUseCase.addState(state)
        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test

    fun `getAllStates() should return success result with list of state when the file have data`() {
        //Given
        val state = listOf(
            createState("2", "done"),
            (createState("3", "in review"))
        )
        every { stateRepository.getAllStates() } returns Result.success(state)
        //  When
        val result = manageStatesUseCase.getAllStates()

        //Then
        assertThat(result.getOrThrow()).isEqualTo(state)
    }

    @Test
    fun `getAllStates() should return failure result with empty data exception when have no data`() {
        //Given & When
        every { stateRepository.getAllStates() } returns Result.success(listOf())
        //  When
        val result = manageStatesUseCase.getAllStates()

        //Then
        assertThrows<DataException.EmptyDataException> { result.getOrThrow() }
    }

    @Test
    fun `getAllStates() should return failure result with read exception when error happens while reading from data`() {
        //Given
        every { stateRepository.getAllStates() } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.getAllStates()

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `getStateIdByName() should return id of state when state exist`() {
        //Given
        val state = State("565", "TODO")
        every { stateRepository.getAllStates() } returns Result.success(listOf(state))

        //  When
        val result = manageStatesUseCase.getStateIdByName(state.name)

        //Then
        assertThat(result).isEqualTo(state.id)
    }

    @Test
    fun `getStateIdByName() should return null when state not exist`() {
        //Given
        val state = State("565", "TODO")
        every { stateRepository.getAllStates() } returns Result.success(listOf(state))

        //  When
        val result = manageStatesUseCase.getStateIdByName("injhb")

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when state name not valid`() {
        //Given
        val state = State("565", "T&^^ODO")
        every { stateRepository.getAllStates() } returns Result.success(listOf())

        //  When
        val result = manageStatesUseCase.getStateIdByName(state.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when getAllState return failure`() {
        //Given
        val state = State("565", "TODO")
        every { stateRepository.getAllStates() } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.getStateIdByName(state.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

}
