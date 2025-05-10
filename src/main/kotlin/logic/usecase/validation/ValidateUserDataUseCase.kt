package logic.usecase.validation

import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException


class ValidateUserDataUseCase {

    fun validateUserName(username: String) {
        if (username.isBlank() || username.length > 20 || username.length < 4 || username.first()
                .isDigit()
        ) throw InvalidUserNameException()
    }

    fun validatePassword(password: String) {
        if (password.isBlank() || password.length < 8) throw InvalidPasswordException()
    }
}