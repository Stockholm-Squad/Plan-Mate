package org.example.di

import AddUserToProjectUIImp
import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.addusertoProject.AddUserToProjectUI
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.features.login.LoginUi
import org.example.ui.features.login.LoginUiImp
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.project.ProjectManagerUiImp
import org.example.ui.features.state.StateManageUi
import org.example.ui.features.state.StateManagerUiImp
import org.example.ui.features.state.admin.AdminStateManagerUi
import org.example.ui.features.state.admin.AdminStateManagerUiImpl
import org.example.ui.features.state.common.UserStateManagerUi
import org.example.ui.features.state.common.UserStateManagerUiImp
import org.example.ui.features.state.mate.MateStateManagerUi
import org.example.ui.features.state.mate.MateStateManagerUiImpl
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.task.TaskManagerUiImp
import org.example.ui.features.user.CreateUserUi
import org.example.ui.features.user.CreateUserUiImp
import org.example.ui.features.common.utils.UiUtils
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory<AuditSystemManagerUi> { AuditSystemManagerUiImp(get(), get(), get()) }
    factory<LoginUi> { LoginUiImp(get(), get(), get()) }
    factory<CreateUserUi> { CreateUserUiImp(get(), get(), get()) }
    factory<ProjectManagerUi> { ProjectManagerUiImp(get(), get(), get(), get(), get(), get(), get()) }
    factory<AddUserToProjectUI> { AddUserToProjectUIImp(get(), get(), get(), get(), get(), get()) }
    factory<UserStateManagerUi> { UserStateManagerUiImp(get(), get()) }
    factory<MateStateManagerUi> { MateStateManagerUiImpl(get()) }
    factory<AdminStateManagerUi> { AdminStateManagerUiImpl(get(), get(), get(), get()) }
    factory<TaskManagerUi> { TaskManagerUiImp(get(), get(), get(), get(), get(), get(), get()) }
    factory<StateManageUi> { StateManagerUiImp(get(), get(), get()) }
    factory<UiUtils> { UiUtils(get()) }
}
