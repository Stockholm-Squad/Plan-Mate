package org.example.di

import data.network.provider.*
import org.example.data.mongo.MongoSetup
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase

val networkModule = module {
    single<CoroutineDatabase> { MongoSetup.createDataBase() }

    single { ProjectMongoProvider(get()) }
    single { StateMongoProvider(get()) }
    single { TaskMongoProvider(get()) }
    single { UserMongoProvider(get()) }
    single { AuditsMongoProvider(get()) }
    single { TaskInProjectMongoProvider(get()) }
    single { UserAssignedToProjectMongoProvider(get()) }
    single { MateTaskAssignmentMongoProvider(get()) }
}