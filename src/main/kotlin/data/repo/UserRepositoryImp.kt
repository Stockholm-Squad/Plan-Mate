package org.example.data.repo

import data.datasources.user_data_source.UserDataSource
import data.models.MateTaskAssignmentModel
import data.models.UserAssignedToProjectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import logic.models.entities.User
import logic.models.exceptions.StateExceptions
import logic.models.exceptions.UserExceptions
import logic.models.exceptions.UserToProjectExceptions
import logic.models.exceptions.UserToTaskExceptions
import org.example.data.datasources.mate_task_assignment_data_source.IMateTaskAssignmentDataSource
import org.example.data.datasources.mate_task_assignment_data_source.MateTaskAssignmentDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.UserAssignedToProjectDataSource
import org.example.data.datasources.user_data_source.IUserDataSource
import org.example.data.mapper.mapToStateModel
import org.example.data.mapper.mapToUserEntity
import org.example.data.mapper.mapToUserModel
import org.example.data.utils.executeSafelyWithContext
import org.example.data.utils.tryToExecute
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.extention.toSafeUUID
import java.util.*

class UserRepositoryImp(
    private val userDataSource: UserDataSource,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
    private val mateTaskAssignment: MateTaskAssignmentDataSource,
) : UserRepository {

    override suspend fun addUser(user: User): Boolean =
        tryToExecute({ userDataSource.addUser(user.mapToUserModel()) },
            onSuccess = { it },
            onFailure = { throw UserExceptions.UserNotAddedException() }
        )

    override suspend fun getAllUsers(): List<User> =
        tryToExecute({ userDataSource.getAllUsers() },
            onSuccess = { it.mapNotNull { userModel -> userModel.mapToUserEntity() } },
            onFailure = { throw UserExceptions.UsersDoesNotExistException() }
        )


    override suspend fun getUsersByProjectId(projectId: UUID): List<User> =
        withContext(Dispatchers.IO) {
            val assignedUsersDeferred = async {
                tryToExecute(
                    function = { userDataSource.getUsersByProjectId(projectId.toString()) },
                    onSuccess = { it },
                    onFailure = { throw UserExceptions.UserDoesNotExistException() }
                )
            }

            val allUsersDeferred = async {
                tryToExecute(
                    function = { userDataSource.getAllUsers() },
                    onSuccess = { it },
                    onFailure = { throw UserExceptions.UserDoesNotExistException() }
                )
            }

            val assignedUsers = assignedUsersDeferred.await()
            val allUsers = allUsersDeferred.await()

            val usersIds = assignedUsers.map { it.username }
            allUsers.filter { it.username in usersIds }
                .mapNotNull { it.mapToUserEntity() }
        }


    override suspend fun addUserToProject(projectId: UUID, userName: String): Boolean =
        tryToExecute({ userAssignedToProjectDataSource.addUserToProject(projectId.toString(), userName) },
            onSuccess = { it },
            onFailure = { throw UserToProjectExceptions.UserNotAddedToProjectException() }
        )


    override suspend fun deleteUserFromProject(projectId: UUID, userName: String): Boolean =
        tryToExecute({ userAssignedToProjectDataSource.deleteUserFromProject(projectId.toString(), userName) },
            onSuccess = { it },
            onFailure = { throw UserToProjectExceptions.UserNotDeletedFromProjectException() }
        )


    override suspend fun addUserToTask(mateName: String, taskId: UUID): Boolean =
        tryToExecute({ mateTaskAssignment.addUserToTask(mateName, taskId.toString()) },
            onSuccess = { it },
            onFailure = {
                throw UserToTaskExceptions.UserNotAddedToTaskException()
            }
        )

    override suspend fun deleteUserFromTask(mateName: String, taskId: UUID): Boolean =
        tryToExecute({ mateTaskAssignment.deleteUserFromTask(mateName, taskId.toString()) },
            onSuccess = { it },
            onFailure = { throw UserToTaskExceptions.UserNotDeletedFromTaskException() }
        )
}