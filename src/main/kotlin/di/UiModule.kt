package org.example.di

import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory { GetAuditSystemUi(get()) }
    factory { GetAuthenticationUi(get()) }
    factory { GetProjectUi(get()) }
    factory { GetTaskUi(get()) }
    factory { GetStateUi(get()) }
}