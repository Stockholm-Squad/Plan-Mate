package org.example.logic.usecase.project

import logic.usecase.login.LoginUseCase
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import java.util.*

class ManageUsersAssignedToProjectUseCase(
    private val userRepository: UserRepository,

    private val loginUseCase: LoginUseCase,

    ) {


}