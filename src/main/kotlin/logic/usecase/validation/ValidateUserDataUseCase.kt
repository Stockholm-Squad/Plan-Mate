package org.example.logic.usecase.common

import org.example.logic.model.exceptions.PlanMateExceptions

class ValidateUserDataUseCase {

     fun validateUserName(username: String) {
        if (username.isBlank() || username.length > 20 || username.length < 4 || username.first()
                .isDigit()
        ) throw PlanMateExceptions.LogicException.InvalidUserName()
    }

     fun validatePassword(password: String) {
        if (password.isBlank() || password.length < 8) throw PlanMateExceptions.LogicException.InvalidPassword()
    }
}