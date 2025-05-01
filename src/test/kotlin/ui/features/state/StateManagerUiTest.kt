package ui.features.state

import io.mockk.*
import logic.model.entities.Role
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.features.state.StateManagerUi
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

    @Test
    fun `launchStateManagerUi() should launch adminStateManagerUi when role is ADMIN`() {
        //Given
        val role = Role.ADMIN
        every { adminStateManagerUi.launchUi() } just runs

        //When
        stateManagerUi.launchStateManagerUi(role)

        //Then
        verify { adminStateManagerUi.launchUi() }
    }

    @Test
    fun `launchStateManagerUi() should launch mateStateManagerUi when role is MATE`() {
        //Given
        val role = Role.MATE
        every { mateStateManagerUi.launchUi() } just runs

        //When
        stateManagerUi.launchStateManagerUi(role)

        //Then
        verify { mateStateManagerUi.launchUi() }
    }
}