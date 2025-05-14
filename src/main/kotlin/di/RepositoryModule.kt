package org.example.di

import org.example.data.repo.*
import org.example.logic.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    factory<AuditRepository> { AuditRepositoryImp(get()) }
    factory<ProjectRepository> { ProjectRepositoryImp(get()) }
    factory<EntityStateRepository> { EntityStateRepositoryImp(get()) }
    factory<TaskRepository> { TaskRepositoryImp(get()) }
    factory<UserRepository> { UserRepositoryImp(get(), get(), get()) }
}