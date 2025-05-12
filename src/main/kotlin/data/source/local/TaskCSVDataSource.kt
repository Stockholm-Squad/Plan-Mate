package org.example.data.source.local

import data.dto.TaskDto
import org.example.data.source.TaskDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class TaskCSVDataSource(
    private val taskReaderWriter: IReaderWriter<TaskDto>,
) : TaskDataSource {
    override suspend fun getAllTasks(): List<TaskDto> =
        taskReaderWriter.read()

    override suspend fun addTask(task: TaskDto): Boolean =
        taskReaderWriter.append(listOf(task))

    override suspend fun editTask(task: TaskDto): Boolean =
        getAllTasks().map { mappedTask -> if (task.id == mappedTask.id) task else mappedTask }
            .let { updatesTasks -> taskReaderWriter.overWrite(updatesTasks) }

    override suspend fun deleteTask(id: String): Boolean =
        getAllTasks().filterNot { task -> id == task.id }
            .let { updatesTasks -> taskReaderWriter.overWrite(updatesTasks) }


    override suspend fun getTasksInProject(taskIds: List<String>): List<TaskDto> =
        getAllTasks().filter { task -> task.id in taskIds }


    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> =
        getAllTasks().filter { task -> task.id in taskIds }
}