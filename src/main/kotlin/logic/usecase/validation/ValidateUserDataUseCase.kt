package logic.usecase.validation


import logic.models.exceptions.UserExceptions


class ValidateUserDataUseCase {

     fun validateUserName(username: String) {
        if (username.isBlank() || username.length > 20 || username.length < 4 || username.first()
                .isDigit()
        ) throw UserExceptions.InvalidUserNameException()
    }

     fun validatePassword(password: String) {
        if (password.isBlank() || password.length < 8) throw UserExceptions.InvalidPasswordException()
    }
}