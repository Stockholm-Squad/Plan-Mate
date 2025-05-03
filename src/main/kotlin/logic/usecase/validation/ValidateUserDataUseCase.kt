package logic.usecase.validation

import org.example.logic.model.exceptions.InvalidPassword
import org.example.logic.model.exceptions.InvalidUserName

class ValidateUserDataUseCase {

     fun validateUserName(username: String) {
        if (username.isBlank() || username.length > 20 || username.length < 4 || username.first()
                .isDigit()
        ) throw InvalidUserName()
    }

     fun validatePassword(password: String) {
        if (password.isBlank() || password.length < 8) throw InvalidPassword()
    }
}