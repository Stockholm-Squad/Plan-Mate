package org.example.di

import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.datasources.task_data_source.TaskMongoDataSource
import org.koin.dsl.module

val dataBaseModule = module {
    single<ITaskDataSource> { TaskMongoDataSource() }
}