package ui.features.state

import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Role
import org.example.ui.features.state.AdminStateManagerUi
import org.example.ui.features.state.MateStateManagerUi
import org.example.ui.features.state.StateManagerUi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

    @AfterEach
    fun tearDown() {
        verify(exactly = 1) { stateManagerUi.launchStateManagerUi(any()) }
    }

    @Test
    fun `launchStateManagerUi() should launch adminStateManagerUi when role is ADMIN`() {
        //Given
        val role = Role.ADMIN

        //When
        stateManagerUi.launchStateManagerUi(role)

        //Then
        verify { adminStateManagerUi.launchUi() }
    }

    @Test
    fun `launchStateManagerUi() should launch mateStateManagerUi when role is MATE`() {
        //Given
        val role = Role.MATE

        //When
        stateManagerUi.launchStateManagerUi(role)

        //Then
        verify { mateStateManagerUi.launchUi() }
    }
}