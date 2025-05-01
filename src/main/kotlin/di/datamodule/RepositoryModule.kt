package org.example.di.datamodule

import org.example.data.repo.*
import org.example.logic.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    factory<AuditSystemRepository> { AuditSystemRepositoryImp(get()) }
    factory<AuthenticationRepository> { AuthenticationRepositoryImp(get()) }
    factory<ProjectRepository> { ProjectRepositoryImp(get()) }
    factory<StateRepository> { StateRepositoryImp(get()) }
    factory<TaskRepository> { TaskRepositoryImp(get(), get()) }


}