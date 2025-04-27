package org.example.di.logicmodule

import auditSystemUseCaseModule
import org.koin.dsl.module
import stateUseCaseModule
import taskUseCaseModule


val useCaseModule = module {
    includes(
        auditSystemUseCaseModule, authenticationUseCaseModule,
        taskUseCaseModule, projectUseCaseModule, stateUseCaseModule
    )
}