package org.example.di

import org.example.data.repo.*
import org.example.logic.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    factory<AuditSystemRepository> { AuditSystemRepositoryImp(get()) }
    factory<ProjectRepository> { ProjectRepositoryImp(get()) }
    factory<ProjectStateRepository> { ProjectStateRepositoryImp(get()) }
    factory<TaskRepository> { TaskRepositoryImp(get(), get(), get()) }
    factory<UserRepository> { UserRepositoryImp(get(), get(), get()) }
}