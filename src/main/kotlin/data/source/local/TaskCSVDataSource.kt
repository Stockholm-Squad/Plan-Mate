package org.example.data.source.local

import data.dto.TaskDto
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.TaskDataSource
import org.example.data.source.TaskInProjectDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class TaskCSVDataSource(
    private val taskReaderWriter: IReaderWriter<TaskDto>,
    private val mateTaskAssignmentDataSource: MateTaskAssignmentDataSource,
    private val taskInProjectDataSource: TaskInProjectDataSource,
) : TaskDataSource {
    override suspend fun getAllTasks(): List<TaskDto> = taskReaderWriter.read()

    override suspend fun addTask(task: TaskDto): Boolean = taskReaderWriter.append(listOf(task))

    override suspend fun updateTask(task: TaskDto): Boolean =
        getAllTasks().map { mappedTask -> if (task.id == mappedTask.id) task else mappedTask }
            .let { updatesTasks -> taskReaderWriter.overWrite(updatesTasks) }

    override suspend fun deleteTask(id: String): Boolean =
        getAllTasks().filterNot { task -> id == task.id }
            .let { updatesTasks -> taskReaderWriter.overWrite(updatesTasks) }


    override suspend fun getTasksInProject(projectId: String): List<TaskDto> =
        taskInProjectDataSource.getTasksInProjectByProjectId(projectId).map { it.taskId }
            .let { taskIds -> getTasksByIds(taskIds) }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> =
        getAllTasks().filter { task -> task.id in taskIds }

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean =
        taskInProjectDataSource.addTaskInProject(projectId = projectId, taskId = taskId)

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean =
        taskInProjectDataSource.deleteTaskFromProject(projectId = projectId, taskId = taskId)

    override suspend fun getAllTasksByUserName(username: String): List<TaskDto> =
        mateTaskAssignmentDataSource.getUsersMateTaskByUserName(username).map { it.taskId }
            .let { mateTaskAssignments -> getTasksByIds(mateTaskAssignments) }
}