package org.example.di.logicmodule

import di.logicmodule.auditSystemUseCaseModule
import di.logicmodule.stateUseCaseModule
import di.logicmodule.taskUseCaseModule
import org.koin.dsl.module


val useCaseModule = module {
    includes(
        auditSystemUseCaseModule, loginUseCaseModule,
        taskUseCaseModule, projectUseCaseModule, stateUseCaseModule, createUserUseCaseModule
    )
}