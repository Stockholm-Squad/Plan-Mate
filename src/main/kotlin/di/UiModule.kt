package org.example.di

import org.example.ui.features.addusertoproject.AddUserToProjectUIImp
import org.example.ui.PlanMateConsoleUi
import org.example.ui.features.addusertoproject.AddUserToProjectUI
import org.example.ui.features.audit.AuditSystemManagerUi
import org.example.ui.features.audit.AuditSystemManagerUiImp
import org.example.ui.features.common.utils.UiUtils
import org.example.ui.features.login.LoginUi
import org.example.ui.features.login.LoginUiImp
import org.example.ui.features.project.ProjectManagerUi
import org.example.ui.features.project.ProjectManagerUiImp
import org.example.ui.features.state.EntityEntityStateManagerUiImp
import org.example.ui.features.state.EntityStateManageUi
import org.example.ui.features.state.admin.AdminEntityStateManagerUi
import org.example.ui.features.state.admin.AdminEntityStateManagerUiImpl
import org.example.ui.features.state.common.UserEntityStateManagerUi
import org.example.ui.features.state.common.UserEntityStateManagerUiImp
import org.example.ui.features.state.mate.MateEntityEntityStateManagerUiImpl
import org.example.ui.features.state.mate.MateEntityStateManagerUi
import org.example.ui.features.task.TaskManagerUi
import org.example.ui.features.task.TaskManagerUiImp
import org.example.ui.features.user.CreateUserUi
import org.example.ui.features.user.CreateUserUiImp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    singleOf(::PlanMateConsoleUi)

    factory<AuditSystemManagerUi> { AuditSystemManagerUiImp(get(), get(), get(), get()) }
    factory<LoginUi> { LoginUiImp(get(), get(), get()) }
    factory<CreateUserUi> { CreateUserUiImp(get(), get(), get()) }
    factory<ProjectManagerUi> { ProjectManagerUiImp(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory<AddUserToProjectUI> { AddUserToProjectUIImp(get(), get(), get(), get(), get()) }
    factory<UserEntityStateManagerUi> { UserEntityStateManagerUiImp(get(), get()) }
    factory<MateEntityStateManagerUi> { MateEntityEntityStateManagerUiImpl(get()) }
    factory<AdminEntityStateManagerUi> { AdminEntityStateManagerUiImpl(get(), get(), get(), get()) }
    factory<TaskManagerUi> { TaskManagerUiImp(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory<EntityStateManageUi> { EntityEntityStateManagerUiImp(get(), get(), get(), get()) }
    factory<UiUtils> { UiUtils(get()) }
}
