package org.example.di

import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.AdminStateManagerUi
import org.example.ui.features.state.AdminStateManagerUiImpl
import org.example.ui.features.state.MateStateManagerUi
import org.example.ui.features.state.MateStateManagerUiImpl
import org.example.ui.features.task.TaskManagerUi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory { AuditSystemManagerUi(get()) }
    factory { AuthenticationManagerUi(get()) }
    factory { ProjectManagerUi(get()) }
    factory { TaskManagerUi(get(), get(), get(), get(), get(), get(), get()) }

    factory<MateStateManagerUi> { MateStateManagerUiImpl(get()) }
    factory<AdminStateManagerUi> { AdminStateManagerUiImpl(get()) }
}