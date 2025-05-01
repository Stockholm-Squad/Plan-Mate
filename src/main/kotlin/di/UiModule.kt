package org.example.di

import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.features.login.LoginUi
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUiImpl
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.common.UserStateManagerUiImp
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUiImpl
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.user.AddUserUi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory { AuditSystemManagerUiImp(get(),get(),get(),get()) }
    factory { LoginUi(get(),get(),get()) }
    factory { AddUserUi(get(),get(),get()) }
    factory { ProjectManagerUi(get(),get(),get(),get(),get(),get(),get()) }
    factory<UserStateManagerUi> { UserStateManagerUiImp(get(), get()) }
    factory<MateStateManagerUi> { MateStateManagerUiImpl(get()) }
    factory<AdminStateManagerUi> { AdminStateManagerUiImpl(get(), get(), get(), get()) }
    factory<TaskManagerUi> { TaskManagerUi(get(), get(), get(), get(), get(), get(), get()) }
}