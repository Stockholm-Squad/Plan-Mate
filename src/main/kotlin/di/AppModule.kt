package org.example.di

import org.example.di.logicmodule.useCaseModule
import org.koin.dsl.module


val appModule = module {
    includes(useCaseModule,uiModule, inputOutputModule)
}