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
            (get<MongoProvider>().provideCollection(PROJECTS_COLLECTION_NAME, ProjectDto::class.java)), get()
        )
    }
    single<TaskDataSource> {
        TaskMongoDataSource(
            (get<MongoProvider>().provideCollection(TASKS_COLLECTION_NAME, TaskDto::class.java)),
            mateTaskAssignmentDataSource = get(),
            taskInProjectDataSource = get(),
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
            userAssignedToProjectDataSource = get(),
            mateTaskAssignmentDataSource = get(),
        )
    }
    single<TaskInProjectDataSource> {
        TaskInProjectMongoDataSource(
            (get<MongoProvider>().provideCollection(TASK_IN_PROJECT_COLLECTION_NAME, TaskInProjectDto::class.java))
        )
    }
    single<MateTaskAssignmentDataSource> {
        MateTaskAssignmentMongoDataSource(
            (get<MongoProvider>().provideCollection(
                MATE_TASK_ASSIGNMENT_COLLECTION_NAME,
                MateTaskAssignmentDto::class.java
            ))
        )
    }
    single<UserAssignedToProjectDataSource> {
        UserAssignedToProjectMongoDataSource(
            (get<MongoProvider>().provideCollection(
                USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME,
                UserAssignedToProjectDto::class.java
            ))
        )
    }
}

val localDataSourceModule = module {
    factory<TaskInProjectDataSource> { TaskInProjectCSVDataSource(get(named("taskInProjectReaderWriter"))) }
    factory<UserAssignedToProjectDataSource> { UserAssignedToProjectCSVDataSource(get(named("userAssignedToProjectReaderWriter"))) }
    factory<MateTaskAssignmentDataSource> { MateTaskAssignmentCSVDataSource(get(named("mateTaskAssignmentReaderWriter"))) }

    factory<AuditDataSource> { AuditCSVDataSource(get(named("auditReaderWriter"))) }
    factory<ProjectDataSource> {
        ProjectCSVDataSource(
            get(named("projectReaderWriter")),
            get()
        )
    }
    factory<EntityStateDataSource> { EntityStateCSVDataSource(get(named("entityStateReaderWriter"))) }
    factory<TaskDataSource> { TaskCSVDataSource(
        get(named("taskReaderWriter")),
        mateTaskAssignmentDataSource = get(),
        taskInProjectDataSource = get()
    ) }
    factory<UserDataSource> {
        UserCSVDataSource(
            get(named("userReaderWriter")),
            userAssignedToProjectDataSource = get(),
            mateTaskAssignmentDataSource = get()
        )
    }
}