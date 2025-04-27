package org.example.di.logicmodule

import org.example.logic.usecase.GetAuditSystemUseCase
import org.example.logic.usecase.GetAuthenticationUseCase
import org.example.logic.usecase.GetProjectUseCase
import org.example.logic.usecase.GetStateUseCase
import org.example.logic.usecase.GetTaskUseCase
import org.koin.dsl.module


val useCaseModule= module {
    single { GetAuditSystemUseCase(get()) }
    single { GetAuthenticationUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { GetStateUseCase(get()) }
}