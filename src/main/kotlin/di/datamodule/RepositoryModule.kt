package org.example.di.datamodule

import org.example.data.repo.AuditSystemRepositoryImp
import org.example.data.repo.AuthenticationRepositoryImp
import org.example.data.repo.ProjectRepositoryImp
import org.example.data.repo.StateRepositoryImp
import org.example.data.repo.TaskRepositoryImp
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.ProjectRepository
import org.example.logic.repository.StateRepository
import org.example.logic.repository.TaskRepository
import org.koin.dsl.module

val repositoryModule = module {
    single <AuditSystemRepository>{ AuditSystemRepositoryImp(get()) }
    single <AuthenticationRepository>{ AuthenticationRepositoryImp(get()) }
    single <ProjectRepository>{ ProjectRepositoryImp(get()) }
    single <StateRepository>{ StateRepositoryImp(get()) }
    single <TaskRepository>{ TaskRepositoryImp(get()) }

}