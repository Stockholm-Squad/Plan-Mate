package org.example.di

import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.logic.usecase.user.CreateUserUseCase
import org.koin.dsl.module


val useCaseModule = module {

    single { CreateUserUseCase(get(), get()) }
    single { ManageAuditSystemUseCase(get()) }
    single { LoginUseCase(get(), get()) }
    factory { ManageTasksUseCase(get()) }

    factory<ManageProjectUseCase> { ManageProjectUseCase(get()) }
    factory<ManageTasksInProjectUseCase> { ManageTasksInProjectUseCase(get(), get()) }
    factory<ManageUsersAssignedToProjectUseCase> { ManageUsersAssignedToProjectUseCase(get()) }

    factory { ManageStatesUseCase(get()) }
}