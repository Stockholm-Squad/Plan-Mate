package ui.features.state

import io.mockk.*
import logic.model.entities.UserRole
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.features.state.StateManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectStateManagerUiTest {
    private lateinit var adminStateManagerUi: AdminStateManagerUi
    private lateinit var mateStateManagerUi: MateStateManagerUi
    private lateinit var stateManagerUi: StateManagerUi
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        adminStateManagerUi = mockk(relaxed = true)
        mateStateManagerUi = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        stateManagerUi = StateManagerUi(adminStateManagerUi, mateStateManagerUi, printer = printer)
    }

    @Test
    fun `launchStateManagerUi() should launch adminStateManagerUi when role is ADMIN`() {
        //Given
        val userRole = UserRole.ADMIN
        every { adminStateManagerUi.launchUi() } just runs

        //When
        stateManagerUi.launchStateManagerUi(userRole)

        //Then
        verify { adminStateManagerUi.launchUi() }
    }

    @Test
    fun `launchStateManagerUi() should launch mateStateManagerUi when role is MATE`() {
        //Given
        val userRole = UserRole.MATE
        every { mateStateManagerUi.launchUi() } just runs

        //When
        stateManagerUi.launchStateManagerUi(userRole)

        //Then
        verify { mateStateManagerUi.launchUi() }
    }

    @Test
    fun `launchStateManagerUi() should print invalid user when role is not MATE or ADMIN`() {
        //Given
        val role = null

        //When
        stateManagerUi.launchStateManagerUi(role)

        //Then
        verify { printer.showMessage("Invalid user") }
    }
}