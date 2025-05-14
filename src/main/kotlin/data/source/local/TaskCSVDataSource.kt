package org.example.data.source.local

import data.dto.MateTaskAssignmentDto
import data.dto.TaskDto
import data.dto.TaskInProjectDto
import org.example.data.source.TaskDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter

class TaskCSVDataSource(
    private val taskReaderWriter: IReaderWriter<TaskDto>,
    private val mateTaskAssignmentReaderWriter: IReaderWriter<MateTaskAssignmentDto>,
    private val taskInProjectReaderWriter: IReaderWriter<TaskInProjectDto>,
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
        getTasksInProjectByProjectId(projectId).map { it.taskId }
            .let { taskIds -> getTasksByIds(taskIds) }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> =
        getAllTasks().filter { task -> task.id in taskIds }

    override suspend fun getAllTasksByUserName(username: String): List<TaskDto> =
        getUsersMateTaskByUserName(username).map { it.taskId }
            .let { mateTaskAssignments -> getTasksByIds(mateTaskAssignments) }

    private suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentReaderWriter.read().filter { mateTaskAssignment -> mateTaskAssignment.taskId == taskId }

    private suspend fun getUsersMateTaskByUserName(username: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentReaderWriter.read().filter { mateTaskAssignment -> mateTaskAssignment.username == username }

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean =
        taskInProjectReaderWriter.append(listOf(TaskInProjectDto(projectId = projectId, taskId = taskId)))

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean =
        getAllTasksInProject().filterNot { taskInProject -> taskInProject.projectId == projectId && taskInProject.taskId == taskId }
            .let { updatedTaskInProjectList -> taskInProjectReaderWriter.overWrite(updatedTaskInProjectList) }

    private suspend fun getAllTasksInProject(): List<TaskInProjectDto> =
        taskInProjectReaderWriter.read()

    private suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> =
        getAllTasksInProject().filter { taskInProject -> taskInProject.projectId == projectId }
}