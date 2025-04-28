package data.datasources.state

import com.google.common.truth.Truth.assertThat
import logic.model.entities.State
import org.example.data.datasources.state.StateCsvDataSource
import org.example.data.datasources.state.StateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions.DataException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StateCsvDataSourceTest {

    private lateinit var stateDataSource: StateDataSource

    @BeforeEach
    fun setUp() {
        stateDataSource = StateCsvDataSource()
    }

    @Test
    fun `editState() should return success result with true when the state updated successfully`() {
        //Given
        val state = State(id = "123", name = "TODO")

        //When
        val result = stateDataSource.editState(state)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return success result with false when the state not exist`() {
        //Given
        val state = State(id = "123", name = "In-progress")

        //When
        val result = stateDataSource.editState(state)

        //Then
        assertThat(result.getOrNull()).isEqualTo(false)
    }

    @Test
    fun `editState() should return failure result with read exception when error happens while reading from the csv file`() {
        //Given
        val state = State(id = "123", name = "In-progress")

        //When
        val result = stateDataSource.editState(state)

        //Then
        assertThrows<DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with write exception when error happens while writing to the csv file`() {
        //Given
        val state = State(id = "123", name = "In-progress")

        //When
        val result = stateDataSource.editState(state)

        //Then
        assertThrows<DataException.WriteException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return success result with true when the id exist and state updated successfully`() {
        //Given
        val id = "123"

        //When
        val result = stateDataSource.deleteState(id)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteState() should return success result with false when the id not exist`() {
        //Given
        val id = "987"

        //When
        val result = stateDataSource.deleteState(id)

        //Then
        assertThat(result.getOrNull()).isEqualTo(false)
    }

    @Test
    fun `deleteState() should return failure result with read exception when error happens while reading from the csv file`() {
        //Given
        val id = "987"

        //When
        val result = stateDataSource.deleteState(id)

        //Then
        assertThrows<DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with write exception when error happens while writing to the csv file`() {
        //Given
        val id = "987"

        //When
        val result = stateDataSource.deleteState(id)

        //Then
        assertThrows<DataException.WriteException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the state add successfully`() {
        //Given
        val state = State(id = "1", name = "Done")

        //When
        val result = stateDataSource.addState(state)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with write exception when error happens while writing into the csv file`() {
        //Given
        val state = State(id = "123", name = "In Review")

        //When
        val result = stateDataSource.addState(state)

        //Then
        assertThrows<DataException.WriteException> { result.getOrThrow() }
    }

    @Test
    fun `getAllStates() should return success result with list of state when the file have data`() {
        //Given & When
        val result = stateDataSource.getAllStates()

        //Then
        assertThat(result.getOrThrow()).isNotEmpty()

    }

    @Test
    fun `getAllStates() should return failure result with empty data exception  when the file is empty`() {
        //Given & When
        val result = stateDataSource.getAllStates()

        //Then
        assertThrows<DataException.EmptyDataException> { result.getOrThrow() }
    }

    @Test
    fun `getAllStates() should return failure result with read exception when error happens while reading from the csv file`() {
        //Given & When
        val result = stateDataSource.getAllStates()

        //Then
        assertThrows<DataException.ReadException> { result.getOrThrow() }
    }

}