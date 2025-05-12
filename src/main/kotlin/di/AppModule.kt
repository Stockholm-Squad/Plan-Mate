package org.example.di

import di.localDataSourceModule
import di.remoteDataSourceModule
import org.koin.dsl.module

val appModule = module {
    includes(
        useCaseModule,
//        localDataSourceModule, readerWriterModule,
        remoteDataSourceModule,
        repositoryModule,
        uiModule,
        inputOutputModule
    )
}