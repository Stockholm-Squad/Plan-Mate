package logic.usecase.validation


class ValidateUserDataUseCase {

    fun isValidUserName(username: String): Boolean {
        val userNameToBeChecked = username.trim()
        return !(userNameToBeChecked.isBlank() || userNameToBeChecked.length > 20 || userNameToBeChecked.length < 4 ||
                userNameToBeChecked.first()
                    .isDigit())
    }

    fun isValidPassword(password: String): Boolean {
        val passwordToBeChecked = password.trim()
        return !(passwordToBeChecked.isBlank() || passwordToBeChecked.length < 8)
    }
}