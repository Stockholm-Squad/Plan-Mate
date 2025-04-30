package data.repo

import com.google.common.truth.Truth.assertThat
import createState
import io.mockk.every
import io.mockk.mockk
import logic.model.entities.State
import org.example.data.datasources.PlanMateDataSource
import org.example.data.repo.StateRepositoryImp
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.model.exceptions.PlanMateExceptions.DataException
import org.example.logic.repository.StateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StateRepositoryImpTest {

    private lateinit var stateDataSource: PlanMateDataSource<State>
    private lateinit var stateRepository: StateRepository
    private lateinit var state: State

    @BeforeEach
    fun setUp() {
        stateDataSource = mockk(relaxed = true)
        state = State(id = "123", name = "In-Progress")
        every { stateDataSource.read() } returns Result.success(listOf())
        stateRepository = StateRepositoryImp(stateDataSource)
    }

    @Test
    fun `editState() should return success result with true when the state updated successfully`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.success(
            value = true
        )
        every { stateDataSource.read() } returns Result.success(listOf(state))

        //When
        val result = stateRepository.editState(state)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return failure result with empty data when file not found`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.success(
            value = true
        )
        every { stateDataSource.read() } returns Result.failure(DataException.FileNotExistException())

        //When
        stateRepository.deleteState("123")
        val result = stateRepository.editState(state)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with throwable when error happens while write failed`() {
        //Given
        every { stateDataSource.read() } returns Result.success(listOf(state))
        every { stateDataSource.write(any()) } returns Result.failure(
            Throwable()
        )

        //When
        val result = stateRepository.editState(state)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with throwable when data is empty`() {
        //Given
        every { stateDataSource.read() } returns Result.success(listOf())

        //When
        val result = stateRepository.editState(state)

        //Then
        assertThrows<PlanMateExceptions.LogicException.StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with throwable when state not exist`() {
        //Given

        every { stateDataSource.read() } returns Result.success(listOf(state))

        //When
        val result = stateRepository.editState(State("0987", ""))

        //Then
        assertThrows<PlanMateExceptions.LogicException.StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return success result with true when state deleted successfully`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.success(
            true
        )
        every { stateDataSource.read() } returns Result.success(listOf(state))

        //When
        val result = stateRepository.deleteState(state.id)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteState() should return failure result with throwable when error happens while writing or reading from the csv file`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.failure(
            Throwable()
        )
        every { stateDataSource.read() } returns Result.success(listOf(state))

        //When
        val result = stateRepository.deleteState(state.id)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when state not exist`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.failure(
            Throwable()
        )
        every { stateDataSource.read() } returns Result.success(listOf(state))

        //When
        val result = stateRepository.deleteState(state.id)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when no states`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.failure(
            Throwable()
        )
        every { stateDataSource.read() } returns Result.success(listOf())

        //When
        val result = stateRepository.deleteState(state.id)

        //Then
        assertThrows<PlanMateExceptions.LogicException.StateNotExistException> { result.getOrThrow() }
    }



    @Disabled
    @Test
    fun `addState() should return success result with true when the state add successfully`() {
        //Given
        val state = State(id = "1", name = "Done")
        every { stateDataSource.write(any()) } returns Result.success(true)

        //When
        val result = stateRepository.addState(state)

        //Then
        assertThat(result.getOrThrow()).isEqualTo(true)
    }

    @Disabled
    @Test
    fun `addState() should return failure result with throwable when error happens while writing into the csv file`() {
        //Given
        val state = State(id = "123", name = "In Review")
        every { stateDataSource.write(any()) } returns Result.failure(
            Throwable()
        )
        //When
        val result = stateRepository.addState(state)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Disabled
    @Test
    fun `getAllStates() should return success result with list of state when the file have data`() {
        //Given
        val stateList = listOf(
            createState(id = "123", name = "In-progress"),
            createState(id = "13", name = "done"),
            createState(id = "10", name = "in review")
        )
        every { stateDataSource.read() } returns Result.success(
            stateList
        )

        //When
        val result = stateRepository.getAllStates()

        //Then
        assertThat(result.getOrThrow()).isEqualTo(stateList)

    }

    @Disabled
    @Test
    fun `getAllStates() should return failure result with empty data exception  when the file is empty`() {
        //Given
        every { stateDataSource.read() } returns Result.success(
            listOf()
        )

        // When
        val result = stateRepository.getAllStates()

        //Then
        assertThrows<DataException.EmptyDataException> { result.getOrThrow() }
    }

    @Disabled
    @Test
    fun `getAllStates() should return failure result with throwable when error happens while reading from data`() {
        //Given
        every { stateDataSource.write(any()) } returns Result.failure(
            Throwable()
        )

        // When
        val result = stateRepository.getAllStates()

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

}