package org.example.di.logicmodule

import org.example.logic.usecase.project.ManageProjectUseCase
import org.koin.dsl.module

val projectUseCaseModule = module {

    factory { ManageProjectUseCase(get()) }
}