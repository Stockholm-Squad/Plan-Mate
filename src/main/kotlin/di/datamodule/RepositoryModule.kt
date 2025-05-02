package org.example.di.datamodule

import logic.model.entities.User
import org.example.data.datasources.PlanMateDataSource
import org.example.data.repo.*
import org.example.logic.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    factory<AuditSystemRepository> { AuditSystemRepositoryImp(get(),get(),) }
    factory<ProjectRepository> { ProjectRepositoryImp(get(),get(),get(),get(),get()) }
    factory<StateRepository> { StateRepositoryImp(get(),get()) }
    factory<TaskRepository> { TaskRepositoryImp(get(), get(),get(),get(),get()) }
    factory<UserRepository> { UserRepositoryImp(get(),get()) }
}