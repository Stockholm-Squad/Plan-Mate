package org.example.di.logicmodule


import org.example.logic.usecase.authentication.AuthenticateUseCase
import org.koin.dsl.module

val authenticationUseCaseModule = module {
    single { AuthenticateUseCase(get()) }
}