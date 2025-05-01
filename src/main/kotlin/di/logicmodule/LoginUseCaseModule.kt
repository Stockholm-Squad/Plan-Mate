package org.example.di.logicmodule


import logic.usecase.login.LoginUseCase
import org.koin.dsl.module

val loginUseCaseModule = module {
    single { LoginUseCase(get()) }
}