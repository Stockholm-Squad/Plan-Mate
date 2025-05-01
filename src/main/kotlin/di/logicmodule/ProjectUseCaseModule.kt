package org.example.di.logicmodule

import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.koin.dsl.module

val projectUseCaseModule = module {
    factory<ManageProjectUseCase> { ManageProjectUseCase(get()) }
    factory<ManageTasksInProjectUseCase> { ManageTasksInProjectUseCase(get(), get()) }
    factory<ManageUsersAssignedToProjectUseCase> { ManageUsersAssignedToProjectUseCase(get(), get()) }
}