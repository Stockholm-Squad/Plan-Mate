package ui.features.state

import io.mockk.*
import logic.model.entities.User
import logic.model.entities.UserRole
import org.example.ui.input_output.output.OutputPrinter
import org.example.ui.features.state.StateManagerUiImp
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectStateManagerUiImpTest {
    private lateinit var adminStateManagerUi: AdminStateManagerUi
    private lateinit var mateStateManagerUi: MateStateManagerUi
    private lateinit var stateManagerUiImp: StateManagerUiImp
    private lateinit var printer: OutputPrinter
    private lateinit var  user: User
    @BeforeEach
    fun setUp() {
        adminStateManagerUi = mockk(relaxed = true)
        mateStateManagerUi = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        stateManagerUiImp = StateManagerUiImp(adminStateManagerUi, mateStateManagerUi, printer = printer)
        user=User(UUID.randomUUID(),"name","hash password")
    }

    @Test
    fun `launchStateManagerUi() should launch adminStateManagerUi when role is ADMIN`() {
        //Given
        val user =User(UUID.randomUUID(),"name","hash password", UserRole.ADMIN)
        every { adminStateManagerUi.launchUi(user) } just runs

        //When
        stateManagerUiImp.launchStateManagerUi(user)

        //Then
        verify { adminStateManagerUi.launchUi(user) }
    }

    @Test
    fun `launchStateManagerUi() should launch mateStateManagerUi when role is MATE`() {

        every { mateStateManagerUi.launchUi(user) } just runs

        //When
        stateManagerUiImp.launchStateManagerUi(user)

        //Then
        verify { mateStateManagerUi.launchUi(user) }
    }

//    @Test
//    fun `launchStateManagerUi() should print invalid user when role is not MATE or ADMIN`() {
//        //Given
//        val user=User(UUID.randomUUID(),"name","hash password",Any())
//        //When
//        stateManagerUiImp.launchStateManagerUi(user)
//
//        //Then
//        verify { printer.showMessage("Invalid user") }
//    }
}