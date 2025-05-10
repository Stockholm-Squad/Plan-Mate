package org.example.di

import logic.usecase.login.LoginUseCase
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.usecase.audit.AddAuditUseCase
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageTasksInProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.logic.usecase.user.AddUserUseCase
import org.koin.dsl.module


val useCaseModule = module {

    single<AddUserUseCase> { AddUserUseCase(get(), get(), get()) }
    single<GetAuditUseCase> { GetAuditUseCase(get(), get(), get()) }
    single<AddAuditUseCase> { AddAuditUseCase(get()) }
    single<LoginUseCase> { LoginUseCase(get(), get()) }
    factory<ManageTasksUseCase> { ManageTasksUseCase(get()) }
    factory<ManageProjectUseCase> { ManageProjectUseCase(get(), get(), get()) }
    factory<GetProjectsUseCase> { GetProjectsUseCase(get()) }
    factory<ManageTasksInProjectUseCase> { ManageTasksInProjectUseCase(get(), get()) }
    factory<ManageUsersAssignedToProjectUseCase> { ManageUsersAssignedToProjectUseCase(get(), get(), get()) }
    factory<ManageStatesUseCase> { ManageStatesUseCase(get()) }
    factory<ValidateUserDataUseCase> { ValidateUserDataUseCase() }
}