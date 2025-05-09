package org.example.data.datasources.task_In_project_data_source


import data.models.TaskInProjectModel
import org.example.data.database.MATE_TASK_ASSIGNMENT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


class TaskInProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : TaskInProjectDataSource {

    private val collection = mongoDatabase.getCollection<TaskInProjectModel>(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean {
        val taskInProject = TaskInProjectModel(taskId, projectId)
        collection.insertOne(taskInProject)
        return true
    }

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean {
        val result =
            collection.deleteOne(TaskInProjectModel::projectId eq projectId, TaskInProjectModel::taskId eq taskId)
        return result.deletedCount > 0
    }

    override suspend fun getAllTasksInProject(): List<TaskInProjectModel> {
        return collection.find().toList()
    }

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectModel> {
        return collection.find(TaskInProjectModel::projectId eq projectId).toList()
    }
}
