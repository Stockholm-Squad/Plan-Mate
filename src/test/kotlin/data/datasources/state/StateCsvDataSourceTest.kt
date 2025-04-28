package data.datasources.state

import com.google.common.truth.Truth.assertThat
import logic.model.entities.State
import org.example.data.datasources.state.StateCsvDataSource
import org.example.data.datasources.state.StateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
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
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with write exception when error happens while writing to the csv file`() {
        //Given
        val state = State(id = "123", name = "In-progress")

        //When
        val result = stateDataSource.editState(state)

        //Then
        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
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
        assertThrows<PlanMateExceptions.DataException.ReadException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with write exception when error happens while writing to the csv file`() {
        //Given
        val id = "987"

        //When
        val result = stateDataSource.deleteState(id)

        //Then
        assertThrows<PlanMateExceptions.DataException.WriteException> { result.getOrThrow() }
    }
}