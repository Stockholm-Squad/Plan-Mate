package org.example.di

import logic.usecase.login.LoginUseCase
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.usecase.audit.AddAuditUseCase
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.logic.usecase.audit.utils.AuditDescriptionProvider
import org.example.logic.usecase.project.*
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.logic.usecase.user.AddUserUseCase
import org.example.logic.utils.DateHandler
import org.example.logic.utils.DateHandlerImp
import org.example.logic.utils.HashingService
import org.example.logic.utils.Md5HashingService
import org.koin.dsl.module


val useCaseModule = module {

    single<HashingService> { Md5HashingService() }
    single<DateHandler> { DateHandlerImp() }

    single<AddUserUseCase> { AddUserUseCase(get(), get(), get(), get()) }
    single<GetAuditUseCase> { GetAuditUseCase(get(), get(), get()) }
    single<AddAuditUseCase> { AddAuditUseCase(get(), get()) }
    single<AuditServicesUseCase> { AuditServicesUseCase(get(), get(), get()) }
    single<AuditDescriptionProvider> { AuditDescriptionProvider() }
    single<LoginUseCase> { LoginUseCase(get(), get()) }
    factory<ManageTasksUseCase> { ManageTasksUseCase(get(), get()) }
    factory<ManageProjectUseCase> { ManageProjectUseCase(get(), get(), get()) }
    factory<ProjectValidationUseCase> { ProjectValidationUseCase(get()) }
    factory<GetProjectsUseCase> { GetProjectsUseCase(get()) }
    factory<ManageUsersAssignedToProjectUseCase> { ManageUsersAssignedToProjectUseCase(get(), get(), get()) }
    factory<ManageEntityStatesUseCase> { ManageEntityStatesUseCase(get()) }
    factory<ValidateUserDataUseCase> { ValidateUserDataUseCase() }
}