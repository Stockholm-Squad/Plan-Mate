package org.example.ui.features.authentication

import org.example.logic.usecase.authentication.ManageAuthenticationUseCase

class AuthenticationManagerUi(
    private val getAuthenticationUseCase: ManageAuthenticationUseCase
) {
}