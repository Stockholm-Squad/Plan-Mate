package org.example

import org.example.di.appModule
import org.example.ui.features.task.TaskManagerUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {

    startKoin {
        modules(appModule)
    }

    val taskManagerUi: TaskManagerUi = getKoin().get()

    taskManagerUi.launchUi()
}