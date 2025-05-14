package di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import data.dto.*
import data.source.remote.mongo.*
import org.example.data.source.*
import org.example.data.source.local.*
import org.example.data.source.remote.MongoSetup
import org.example.data.source.remote.provider.MongoProvider
import org.example.data.source.remote.provider.MongoProviderImpl
import org.example.data.utils.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single<MongoDatabase> { MongoSetup.database }
    single<MongoProvider> { MongoProviderImpl(get()) }

    single<AuditDataSource> {
        AuditMongoDataSource(
            get<MongoProvider>().provideCollection(AUDITS_COLLECTION_NAME, AuditDto::class.java)
        )
    }
    single<ProjectDataSource> {
        ProjectMongoDataSource(
            (get<MongoProvider>().provideCollection(PROJECTS_COLLECTION_NAME, ProjectDto::class.java)),
            (get<MongoProvider>().provideCollection(USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME, UserAssignedToProjectDto::class.java)),
        )
    }
    single<TaskDataSource> {
        TaskMongoDataSource(
            (get<MongoProvider>().provideCollection(TASKS_COLLECTION_NAME, TaskDto::class.java)),
            (get<MongoProvider>().provideCollection(MATE_TASK_ASSIGNMENT_COLLECTION_NAME, MateTaskAssignmentDto::class.java)),
            (get<MongoProvider>().provideCollection(TASK_IN_PROJECT_COLLECTION_NAME, TaskInProjectDto::class.java)),
        )
    }
    single<EntityStateDataSource> {
        EntityStateMongoDataSource(
            (get<MongoProvider>().provideCollection(STATES_COLLECTION_NAME, EntityStateDto::class.java))
        )
    }
    single<UserDataSource> {
        UserMongoDataSource(
            (get<MongoProvider>().provideCollection(USERS_COLLECTION_NAME, UserDto::class.java)),
            (get<MongoProvider>().provideCollection(MATE_TASK_ASSIGNMENT_COLLECTION_NAME, MateTaskAssignmentDto::class.java)),
            (get<MongoProvider>().provideCollection(USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME, UserAssignedToProjectDto::class.java)),
        )
    }
    factory<CurrentUserDataSource> {
        CurrentUserMemoryDataSource()
    }
}

val localDataSourceModule = module {
    factory<AuditDataSource> { AuditCSVDataSource(get(named("auditReaderWriter"))) }
    factory<ProjectDataSource> {
        ProjectCSVDataSource(
            get(named("projectReaderWriter")),
            get(named("userAssignedToProjectReaderWriter")),
        )
    }
    factory<EntityStateDataSource> { EntityStateCSVDataSource(get(named("entityStateReaderWriter"))) }
    factory<TaskDataSource> { TaskCSVDataSource(
        get(named("taskReaderWriter")),
        get(named("mateTaskAssignmentReaderWriter")),
        get(named("taskInProjectReaderWriter")),
    ) }
    factory<UserDataSource> {
        UserCSVDataSource(
            get(named("userReaderWriter")),
            get(named("mateTaskAssignmentReaderWriter")),
            get(named("userAssignedToProjectReaderWriter")),
        )
    }
    factory<CurrentUserDataSource> {
        CurrentUserMemoryDataSource()
    }
}