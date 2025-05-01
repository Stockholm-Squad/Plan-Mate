package org.example.di.logicmodule


import org.example.logic.usecase.user.CreateUserUseCase
import org.koin.dsl.module

val createUserUseCaseModule = module {
    single { CreateUserUseCase(get()) }
}