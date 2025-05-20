package ui.features.state

import io.mockk.*
import logic.usecase.login.LoginUseCase
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.ui.features.common.utils.UiMessages
import org.example.ui.features.state.AdminEntityStateManagerUi
import org.example.ui.features.state.EntityStateManagerUi
import org.example.ui.features.state.ShowAllEntityStateManagerUi
import org.example.ui.input_output.output.OutputPrinter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class EntityStateManagerUiTest {
    private lateinit var adminStateManagerUi: AdminEntityStateManagerUi
    private lateinit var showAllEntityStateManagerUi: ShowAllEntityStateManagerUi
    private lateinit var stateManagerUiImp: EntityStateManagerUi
    private lateinit var printer: OutputPrinter
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        adminStateManagerUi = mockk(relaxed = true)
        showAllEntityStateManagerUi = mockk(relaxed = true)
        printer = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)

        stateManagerUiImp = EntityStateManagerUi(
            adminStateManagerUi, showAllEntityStateManagerUi, printer = printer,
            loginUseCase = loginUseCase
        )
        user = User(UUID.randomUUID(), "name", "hash password")
    }

    @Test
    fun `launchStateManagerUi() should launch adminStateManagerUi when role is ADMIN`() {
        //Given
        val user = User(UUID.randomUUID(), "name", "hash password", UserRole.ADMIN)
        every { adminStateManagerUi.launchUi() } just runs
        every { loginUseCase.getCurrentUser() } returns user

        //When
        stateManagerUiImp.launchUi()

        //Then
        verify { adminStateManagerUi.launchUi() }
    }

    @Test
    fun `launchStateManagerUi() should launch showAllEntityStateManagerUi when role is MATE`() {
        //Given
        every { showAllEntityStateManagerUi.launchUi() } just runs
        every { loginUseCase.getCurrentUser() } returns user

        //When
        stateManagerUiImp.launchUi()

        //Then
        verify { showAllEntityStateManagerUi.launchUi() }
    }

    @Test
    fun `launchStateManagerUi() should print invalid user when user is null`() {
        //Given
        every { loginUseCase.getCurrentUser() } returns null

        //When
        stateManagerUiImp.launchUi()

        //Then
        verify { printer.showMessageLine(UiMessages.INVALID_USER) }
    }


}