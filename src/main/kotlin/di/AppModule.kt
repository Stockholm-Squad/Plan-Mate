package org.example.di

import di.remoteDataSourceModule
import org.koin.dsl.module

val appModule = module {
    includes(
        useCaseModule,
        networkModule,
        remoteDataSourceModule,
//        localDatasourceModule,
        repositoryModule,
        uiModule,
        inputOutputModule
    )
}