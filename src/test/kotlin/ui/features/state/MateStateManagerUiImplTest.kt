package ui.features.state

import io.mockk.*
import org.example.input_output.output.OutputPrinter
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUiImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MateStateManagerUiImplTest {
    private lateinit var mateStateManagerUi: MateStateManagerUi
    private lateinit var userStateManagerUi: UserStateManagerUi
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp() {
        printer = mockk(relaxed = true)
        userStateManagerUi = mockk(relaxed = true)
        mateStateManagerUi = MateStateManagerUiImpl(userStateManagerUi, printer)

    }

    @Test
    fun `launch() should call a show all function from parent class and print list of data when called`() {
        //Given
        every { userStateManagerUi.showAllStates() } just runs

        //When
        mateStateManagerUi.launchUi()

        //Then
        verify { userStateManagerUi.showAllStates() }
    }
}