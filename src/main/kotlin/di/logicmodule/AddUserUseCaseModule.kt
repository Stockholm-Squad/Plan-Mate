package org.example.di.logicmodule


import org.example.logic.usecase.user.AddUserUseCase
import org.koin.dsl.module

val addUserUseCaseModule = module {
    single { AddUserUseCase(get()) }
}