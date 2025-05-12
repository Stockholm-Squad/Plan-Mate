package org.example.data.source.local

import data.dto.TaskInProjectDto
import org.example.data.source.TaskInProjectDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class TaskInProjectCSVDataSource(
    private val taskInProjectReaderWriter: IReaderWriter<TaskInProjectDto>,
) : TaskInProjectDataSource {
    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean =
        taskInProjectReaderWriter.append(listOf(TaskInProjectDto(projectId = projectId, taskId = taskId)))

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean =
        getAllTasksInProject().filterNot { taskInProject -> taskInProject.projectId == projectId && taskInProject.taskId == taskId }
            .let { updatedTaskInProjectList -> taskInProjectReaderWriter.overWrite(updatedTaskInProjectList) }

    override suspend fun getAllTasksInProject(): List<TaskInProjectDto> =
        taskInProjectReaderWriter.read()

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> =
        getAllTasksInProject().filter { taskInProject -> taskInProject.projectId == projectId }
}