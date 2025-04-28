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
    fun `addState() should return success result with true when the name of state is valid`() {
        //Given
        val state = State(id = "111", name = "do")
        every { stateRepository.addState(state) } returns Result.success(true)
        //When
        val result = manageStatesUseCase.addState(state)
        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return success result with true when the name of state have leading and trailing space`() {
        //Given
        val state = State(id = "1", name = "    ToDo    ")
        every { stateRepository.addState(state) } returns Result.success(true)
        //When
        val result = manageStatesUseCase.addState(state)
        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given

        val state = State(id = "7", name = "#In Review$")
        every { stateRepository.addState(state) } returns Result.failure(LogicException.NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.addState(state)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val state = State(id = "4", name = "1In Rev3ew")
        every { stateRepository.addState(state) } returns Result.failure(LogicException.NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.addState(state)

        //Then
        assertThrows<LogicException.NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val state = State(id = "43", name = "")
        every { stateRepository.addState(state) } returns Result.failure(LogicException.NotAllowedStateNameException())

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
    fun `getAllStates() should return failure result with list of state when the file have data`() {
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

}
