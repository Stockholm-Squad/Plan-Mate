package ui.features.state

import io.mockk.*
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.ui.features.state.EntityEntityStateManagerUiImp
import org.example.ui.features.state.admin.AdminEntityStateManagerUi
import org.example.ui.features.state.mate.MateEntityStateManagerUi
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectStateManagerUiImpTest {
    private lateinit var adminStateManagerUi: AdminEntityStateManagerUi
    private lateinit var mateStateManagerUi: MateEntityStateManagerUi
    private lateinit var stateManagerUiImp: EntityEntityStateManagerUiImp
    private lateinit var printer: OutputPrinter
    private lateinit var  user: User
    @BeforeEach
    fun setUp() {
        adminStateManagerUi = mockk(relaxed = true)
        mateStateManagerUi = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        stateManagerUiImp = EntityEntityStateManagerUiImp(adminStateManagerUi, mateStateManagerUi, printer = printer)
        user= User(UUID.randomUUID(),"name","hash password")
    }

    @Test
    fun `launchStateManagerUi() should launch adminStateManagerUi when role is ADMIN`() {
        //Given
        val user = User(UUID.randomUUID(),"name","hash password", UserRole.ADMIN)
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


}