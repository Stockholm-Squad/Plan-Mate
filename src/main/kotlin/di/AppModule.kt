package org.example.di

import datasourceModule
import org.example.di.datamodule.repositoryModule
import org.example.di.logicmodule.useCaseModule
import org.koin.dsl.module

val appModule = module {
    includes(
        useCaseModule,
        datasourceModule,
        repositoryModule,
        uiModule,
        inputOutputModule
    )
}