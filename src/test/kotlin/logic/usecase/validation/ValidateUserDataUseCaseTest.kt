package logic.usecase.validation

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ValidateUserDataUseCaseTest {

    private val validateUserDataUseCase = ValidateUserDataUseCase()

    @ParameterizedTest
    @ValueSource(strings = ["validUser", "user123", "someUser", "User_2025", "goodOne"])
    fun `isValidUserName should return true when username is valid`(username: String) {
        // When
        val result = validateUserDataUseCase.isValidUserName(username)

        // Then
        assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "1user", "abc", "thisusernameiswaytoolongtobeaccepted"])
    fun `isValidUserName should return true when username is inValid`(username: String) {
        // When
        val result = validateUserDataUseCase.isValidUserName(username)

        // Then
        assertThat(result).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["Password123", "mySecurePass!", "12345678", "validPass", "complexPassword2025"])
    fun `isValidPassword should return true when password is valid`(password: String) {
        // When
        val result = validateUserDataUseCase.isValidPassword(password)

        // Then
        assertThat(result).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123", "pass", "short7"])
    fun `isValidPassword should return true when password is inValid`(password: String) {
        // When
        val result = validateUserDataUseCase.isValidPassword(password)

        // Then
        assertThat(result).isFalse()
    }
}
