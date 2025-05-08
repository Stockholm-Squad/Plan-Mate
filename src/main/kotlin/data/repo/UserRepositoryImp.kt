package org.example.data.repo

import data.models.MateTaskAssignmentModel
import data.models.UserAssignedToProjectModel
import logic.models.entities.User
import logic.models.exceptions.UserExceptions
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.mapper.mapToUserEntity
import org.example.data.mapper.mapToUserModel
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.extention.toSafeUUID
import java.util.*

class UserRepositoryImp(
    private val userDataSource: IUserDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
    private val mateTaskAssignment: IMateTaskAssignmentDataSource,
) : UserRepository {
    override suspend fun addUser(user: User): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                userDataSource.append(listOf(user.mapToUserModel()))
            },
            onFailure = {
                throw UserExceptions.UserNotAddedException()
            }
        )

    override suspend fun getAllUsers(): List<User> =
        executeSafelyWithContext(
            onSuccess = {
                userDataSource.read()
                    .mapNotNull {
                        it.mapToUserEntity()
                    }
            },
            onFailure = {
                throw UserExceptions.UserDoesNotExistException()
            }
        )


    override suspend fun getUsersByProjectId(projectId: UUID): List<User> =
        executeSafelyWithContext(
            onSuccess = {
                val assignedUser = userAssignedToProjectDataSource.read().filter {
                    it.projectId == projectId.toString()
                }.map { it.userName }
                val allUsers = userDataSource.read()
                allUsers.filter { it.username in assignedUser }
                    .mapNotNull { it.mapToUserEntity() }
            },
            onFailure = {
                throw UserExceptions.UserDoesNotExistException()
            }
        )


    override suspend fun addUserToProject(projectId: UUID, userName: String): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                userAssignedToProjectDataSource.append(
                    listOf(UserAssignedToProjectModel(projectId = projectId.toString(), userName = userName))
                )
            }, onFailure = {
                throw UserExceptions.UserNotAddedException()
            }
        )


    override suspend fun deleteUserFromProject(projectId: UUID, userName: String): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                userAssignedToProjectDataSource.read().filter {
                    (it.projectId.toSafeUUID() == projectId) && (it.userName == userName)
                }.let { userAssignedToProjectDataSource.overWrite(it) }
            },
            onFailure = {
                throw UserExceptions.UserNotDeletedException()
            }
        )


    override suspend fun addUserToTask(mateName: String, taskId: UUID): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                mateTaskAssignment.append(
                    listOf(
                        MateTaskAssignmentModel(userName = mateName, taskId = taskId.toString())
                    )
                )
            }, onFailure = {
                throw UserExceptions.UserNotAddedException()
            }
        )

    override suspend fun deleteUserFromTask(mateName: String, taskId: UUID): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                val originalAssignments = mateTaskAssignment.read()
                val newAssignments = mateTaskAssignment.read().filterNot {
                    it.userName == mateName && it.taskId == taskId.toString()
                }
                return@executeSafelyWithContext if (newAssignments.size != originalAssignments.size) {
                    mateTaskAssignment.overWrite(newAssignments)
                    true
                } else {
                    false
                }
            },
            onFailure = {
                throw UserExceptions.UserNotDeletedException()
            }
        )
}