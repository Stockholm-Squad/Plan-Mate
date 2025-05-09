package org.example.di

import org.koin.dsl.module

val appModule = module {
    includes(
        useCaseModule,
//        datasourceModule,
        repositoryModule,
        uiModule,
        inputOutputModule,
        dataBaseModule
    )
}