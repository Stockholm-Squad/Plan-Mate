package org.example.data.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.example.data.mapper.mapToUserEntity
import org.example.data.mapper.mapToUserModel
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.UserDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.*
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import java.util.*

class UserRepositoryImp(
    private val userDataSource: UserDataSource,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
    private val mateTaskAssignment: MateTaskAssignmentDataSource,
) : UserRepository {

    override suspend fun addUser(user: User): Boolean =
        tryToExecute(
            { userDataSource.addUser(user.mapToUserModel()) },
            onSuccess = { success -> success },
            onFailure = { throw UserNotAddedException() }
        )

    override suspend fun getAllUsers(): List<User> =
        tryToExecute(
            { userDataSource.getAllUsers() },
            onSuccess = { listOfUsers -> listOfUsers.mapNotNull { user -> user.mapToUserEntity() } },
            onFailure = { throw UsersDoesNotExistException() }
        )


    override suspend fun getUsersByProjectId(projectId: UUID): List<User> =
        withContext(Dispatchers.IO) {
            val assignedUsersDeferred = async {
                tryToExecute(
                    function = { userDataSource.getUsersByProjectId(projectId.toString()) },
                    onSuccess = { listOfUsers -> listOfUsers },
                    onFailure = { throw UserDoesNotExistException() }
                )
            }

            val allUsersDeferred = async {
                tryToExecute(
                    function = { userDataSource.getAllUsers() },
                    onSuccess = { listOfUsers -> listOfUsers },
                    onFailure = { throw UserDoesNotExistException() }
                )
            }

            val assignedUsers = assignedUsersDeferred.await()
            val allUsers = allUsersDeferred.await()

            val usersIds = assignedUsers.map { user -> user.username }
            allUsers.filter { user -> user.username in usersIds }
                .mapNotNull { it.mapToUserEntity() }
        }


    override suspend fun addUserToProject(projectId: UUID, userName: String): Boolean =
        tryToExecute(
            { userAssignedToProjectDataSource.addUserToProject(projectId.toString(), userName) },
            onSuccess = { success -> success },
            onFailure = { throw UserNotAddedToProjectException() }
        )


    override suspend fun deleteUserFromProject(projectId: UUID, userName: String): Boolean =
        tryToExecute(
            { userAssignedToProjectDataSource.deleteUserFromProject(projectId.toString(), userName) },
            onSuccess = { success -> success },
            onFailure = { throw UserNotDeletedFromProjectException() }
        )


    override suspend fun addUserToTask(mateName: String, taskId: UUID): Boolean =
        tryToExecute(
            { mateTaskAssignment.addUserToTask(mateName, taskId.toString()) },
            onSuccess = { success -> success },
            onFailure = {
                throw UserNotAddedToTaskException()
            }
        )

    override suspend fun deleteUserFromTask(mateName: String, taskId: UUID): Boolean =
        tryToExecute(
            { mateTaskAssignment.deleteUserFromTask(mateName, taskId.toString()) },
            onSuccess = { success -> success },
            onFailure = { throw UserNotDeletedFromTaskException() }
        )
}