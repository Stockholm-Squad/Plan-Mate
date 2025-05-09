package org.example.data.datasources.task_In_project_data_source


import data.models.TaskInProjectModel
import org.example.data.database.TASK_IN_PROJECT_COLLECTION_NAME
import org.example.data.datasources.project_data_source.ProjectMongoDataSource
import org.example.data.models.ProjectStateModel
import org.litote.kmongo.coroutine.CoroutineDatabase

class TaskInProjectMongoDataSource(mongoDatabase: CoroutineDatabase,
    private val projectMongoDataSource: ProjectMongoDataSource) : TaskInProjectDataSource{

    private val collection = mongoDatabase.getCollection<TaskInProjectModel>(TASK_IN_PROJECT_COLLECTION_NAME)
    override  suspend fun addTaskInProject(projectId: String, taskId: String): Boolean {
         val project = projectMongoDataSource.getAllProjects().find { it.id==projectId }
          collection.insertOne()
    }

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean {

    }

    override suspend fun getAllTasksInProject(): List<TaskInProjectModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskInProjectByProjectId(projectId: String): ProjectStateModel {
        TODO("Not yet implemented")
    }


}
