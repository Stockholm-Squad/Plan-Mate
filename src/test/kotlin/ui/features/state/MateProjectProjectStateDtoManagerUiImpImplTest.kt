package ui.features.state

import io.mockk.mockk
import org.example.ui.features.state.common.UserEntityStateManagerUi
import org.example.ui.features.state.mate.MateEntityEntityStateManagerUiImpl
import org.example.ui.features.state.mate.MateEntityStateManagerUi
import org.junit.jupiter.api.BeforeEach

class MateProjectProjectStateDtoManagerUiImpImplTest {
    private lateinit var mateStateManagerUi: MateEntityStateManagerUi
    private lateinit var userStateManagerUi: UserEntityStateManagerUi

    @BeforeEach
    fun setUp() {
        userStateManagerUi = mockk(relaxed = true)
        mateStateManagerUi = MateEntityEntityStateManagerUiImpl(userStateManagerUi)

    }

//    @Test
//    fun `launch() should call a show all function from parent class and print list of data when called`() {
//        //Given
//        every { userStateManagerUi.showAllStates() } just runs
//
//        //When
//        mateStateManagerUi.launchUi()
//
//        //Then
//        verify { userStateManagerUi.showAllStates() }
//    }
}