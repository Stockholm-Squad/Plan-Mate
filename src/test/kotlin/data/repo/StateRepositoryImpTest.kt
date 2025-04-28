package data.repo

import io.mockk.mockk
import org.example.data.datasources.state.StateDataSource
import org.example.data.repo.StateRepositoryImp
import org.example.logic.repository.StateRepository
import org.junit.jupiter.api.BeforeEach

class StateRepositoryImpTest {

    private lateinit var stateDataSource: StateDataSource
    private lateinit var stateRepository: StateRepository

    @BeforeEach
    fun setUp() {
        stateDataSource = mockk(relaxed = true)
        stateRepository = StateRepositoryImp(stateDataSource)
    }

}