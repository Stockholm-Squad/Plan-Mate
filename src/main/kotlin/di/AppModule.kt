package org.example.di

import di.datamodule.datasourceModule
import org.koin.dsl.module

val appModule = module {
    includes(
        useCaseModule,
        datasourceModule,
        repositoryModule,
        uiModule,
        inputOutputModule,
        dataBaseModule
    )
}