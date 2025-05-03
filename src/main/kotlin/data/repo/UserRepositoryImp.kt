package org.example.data.repo

import data.models.MateTaskAssignment
import data.models.UserAssignedToProject
import logic.model.entities.User
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.extention.toSafeUUID
import org.example.data.mapper.UserMapper
import org.example.logic.repository.UserRepository
import java.util.*

class UserRepositoryImp(
    private val userDataSource: IUserDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
    private val userMapper: UserMapper,
) : UserRepository {
    override fun addUser(user: User): Result<Boolean> {
        return userDataSource.append(listOf(userMapper.mapToUserModel(user)))
    }

    override fun getAllUsers(): Result<List<User>> {
        return userDataSource.read().fold(
            onSuccess = { userModels -> Result.success(userModels.map { userMapper.mapToUserEntity(it) }) },
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
                                users.filter { user -> userNames.contains(user.username) }.map { userModel ->
                                    userMapper.mapToUserEntity(userModel)
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
            listOf(UserAssignedToProject(projectId = projectId.toString(), userName = userName))
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
            listOf(MateTaskAssignment(userName = mateName, taskId = taskId.toString()))
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