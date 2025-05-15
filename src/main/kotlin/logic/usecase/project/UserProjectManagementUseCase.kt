package org.example.logic.usecase.project

import org.example.logic.ProjectNotFoundException
import org.example.logic.TaskNotAddedException
import org.example.logic.TaskNotDeletedException
import org.example.logic.TasksNotFoundException
import org.example.logic.entities.Project
import org.example.logic.entities.Task
import org.example.logic.repository.ProjectRepository
import org.example.logic.repository.TaskRepository
import java.util.UUID

class UserProjectManagementUseCase(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
) {

    suspend fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects()
    }

    suspend fun getProjectByName(projectName: String): Project {
        return getProjectFromList(
            projectName,
            projectRepository.getAllProjects(),
        )
    }

    private fun getProjectFromList(projectName: String, allProjects: List<Project>): Project {
        return allProjects.find { project ->
            project.title == projectName
        } ?: throw ProjectNotFoundException()
    }

    suspend fun getTasksInProjectByName(projectName: String): List<Task> {
        return getProjectByName(projectName).let { project ->
            taskRepository.getTasksInProject(project.id).takeIf { it.isNotEmpty() }
                ?: throw TasksNotFoundException()
        }
    }

    suspend fun getTasksInProjectById(projectId: UUID): List<Task> {
        return taskRepository.getTasksInProject(projectId).takeIf { it.isNotEmpty() }
            ?: throw TasksNotFoundException()
    }

    suspend fun addTaskToProject(projectId: UUID, taskId: UUID): Boolean =
        taskRepository.addTaskInProject(projectId, taskId).also { isAdded ->
            if (!isAdded) throw TaskNotAddedException()
        }

    suspend fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean {
        return taskRepository.deleteTaskFromProject(projectId, taskId).also { isDeleted ->
            if (!isDeleted) throw TaskNotDeletedException()
        }
    }

    suspend fun getAllTasksByUserName(userName: String): List<Task> {
        return taskRepository.getAllTasksByUserName(userName).takeIf { it.isNotEmpty() }
            ?: throw TasksNotFoundException()
    }
}