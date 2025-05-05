package org.example.data.repo

import data.models.MateTaskAssignmentModel
import data.models.UserAssignedToProjectModel
import logic.models.entities.User
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.mapper.mapToUserEntity
import org.example.data.mapper.mapToUserModel
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.extention.toSafeUUID
import java.util.*

class UserRepositoryImp(
    private val userDataSource: IUserDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
) : UserRepository {
    override fun addUser(user: User): Result<Boolean> {
        return userDataSource.append(listOf(user.mapToUserModel()))
    }

    override fun getAllUsers(): Result<List<User>> {
        return userDataSource.read().fold(
            onSuccess = { userModels -> Result.success(userModels.mapNotNull { it.mapToUserEntity() }) },
            onFailure = { Result.failure(it) })
    }

    override fun getUsersByProjectId(projectId: UUID): Result<List<User>> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { userAssignedToProject ->
                userDataSource.read().fold(
                    onSuccess = { users ->
                        userAssignedToProject.filter {
                            projectId == it.projectId.toSafeUUID()
                        }.map { it.userName }
                            .let { userNames ->
                                users.filter { user -> userNames.contains(user.username) }.mapNotNull { userModel ->
                                    userModel.mapToUserEntity()
                                }.let {
                                    Result.success(it)
                                }
                            }
                    },
                    onFailure = { Result.failure(it) }
                )

            }, onFailure = { Result.failure(it) })
    }

    override fun addUserToProject(projectId: UUID, userName: String): Result<Boolean> {
        return userAssignedToProjectDataSource.append(
            listOf(UserAssignedToProjectModel(projectId = projectId.toString(), userName = userName))
        )
    }

    override fun deleteUserFromProject(projectId: UUID, userName: String): Result<Boolean> {
        return userAssignedToProjectDataSource.read().fold(onSuccess = { usersAssignedToProject ->
            usersAssignedToProject.filterNot { userAssignedToProject ->
                (userAssignedToProject.projectId.toSafeUUID() == projectId) && (userAssignedToProject.userName == userName)
            }.let { newUsersAssignedToProject ->
                userAssignedToProjectDataSource.overWrite(newUsersAssignedToProject)
            }
        }, onFailure = { Result.failure(it) })
    }

    override fun addUserToTask(mateName: String, taskId: UUID): Result<Boolean> {
        return mateTaskAssignment.append(
            listOf(MateTaskAssignmentModel(userName = mateName, taskId = taskId.toString()))
        )
    }

    override fun deleteUserFromTask(mateName: String, taskId: UUID): Result<Boolean> {
        return mateTaskAssignment.read().fold(
            onSuccess = { assignments ->
                val newAssignments = assignments.filterNot {
                    it.userName == mateName && it.taskId == taskId.toString()
                }
                when (newAssignments.size == assignments.size) {
                    true -> Result.success(false)
                    false -> mateTaskAssignment.overWrite(newAssignments)
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

}