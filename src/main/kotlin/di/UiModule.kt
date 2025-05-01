package org.example.di

import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.authentication.AuthenticationManagerUi
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.common.UserStateManagerUiImp
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUiImpl
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUiImpl
import org.example.ui.features.task.admin.TaskManagerUiMateImp
import org.example.ui.features.task.mate.TaskManagerUiMateMateImp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory { AuditSystemManagerUi(get(),get(),get(),get()) }
    factory { AuthenticationManagerUi(get()) }
    factory { ProjectManagerUi(get(),get(),get(),get(),get(),get(),get(),get()) }
    factory { TaskManagerUiMateImp(get()) }
    factory { TaskManagerUiMateMateImp(get()) }

    factory<UserStateManagerUi> { UserStateManagerUiImp(get(), get()) }
    factory<MateStateManagerUi> { MateStateManagerUiImpl(get()) }
    factory<AdminStateManagerUi> { AdminStateManagerUiImpl(get(), get(), get(), get()) }
}