package org.example.data.repo

import org.example.data.source.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.example.logic.entities.User
import org.example.logic.UserExceptions
import org.example.logic.UserToProjectExceptions
import org.example.logic.UserToTaskExceptions
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.mapper.mapToUserEntity
import org.example.data.mapper.mapToUserModel
import org.example.data.utils.tryToExecute
import org.example.logic.repository.UserRepository
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