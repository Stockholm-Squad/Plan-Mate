package data.datasources.state

import org.example.data.datasources.state.StateCsvDataSource
import org.example.data.datasources.state.StateDataSource
import org.junit.jupiter.api.BeforeEach

class StateCsvDataSourceTest {

    private lateinit var stateDataSource: StateDataSource

    @BeforeEach
    fun setUp() {
        stateDataSource = StateCsvDataSource()
    }
}