package ui.features.state

import io.mockk.mockk
import org.example.ui.features.state.AdminStateManagerUi
import org.example.ui.features.state.MateStateManagerUi
import org.example.ui.features.state.StateManagerUi
import org.junit.jupiter.api.BeforeEach

class StateManagerUiTest {
    private lateinit var adminStateManagerUi: AdminStateManagerUi
    private lateinit var mateStateManagerUi: MateStateManagerUi
    private lateinit var stateManagerUi: StateManagerUi
    @BeforeEach
    fun setUp() {
        adminStateManagerUi = mockk(relaxed = true)
        mateStateManagerUi = mockk(relaxed = true)
        stateManagerUi = StateManagerUi(adminStateManagerUi, mateStateManagerUi)
    }
}