package org.example

import org.example.data.datasources.MateTaskAssignmentCsvDataSource
import org.example.data.datasources.TaskCsvDataSource
import org.example.data.datasources.TaskInProjectCsvDataSource
import org.example.data.repo.TaskRepositoryImp
import org.example.di.appModule
import org.example.input_output.input.InputReaderImplementation
import org.example.input_output.output.OutputPrinterImplementation
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.task.TaskManagerUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {

    startKoin {
        modules(appModule)
    }

    val taskManagerUi: TaskManagerUi = getKoin().get()

    taskManagerUi.startTaskManager()
}