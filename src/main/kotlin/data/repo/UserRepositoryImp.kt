package org.example.data.repo

import org.example.data.mapper.mapToUserEntity
import org.example.data.mapper.mapToUserModel
import org.example.data.source.CurrentUserDataSource
import org.example.data.source.UserDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.*
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.HashingService
import java.util.*

class UserRepositoryImp(
    private val userDataSource: UserDataSource,
    private val currentUserDataSource: CurrentUserDataSource,
    private val hashingService: HashingService,
) : UserRepository {

    override suspend fun addUser(user: User): Boolean = tryToExecute(
        function = { userDataSource.addUser(user.mapToUserModel()) },
        onSuccess = { isAdded -> isAdded },
        onFailure = { throw UserNotAddedException() }
    )

    override suspend fun getAllUsers(): List<User> = tryToExecute(
        function = { userDataSource.getAllUsers() },
        onSuccess = { listOfUsers -> listOfUsers.mapNotNull { user -> user.mapToUserEntity() } },
        onFailure = { throw UsersDoesNotExistException() }
    )


    override suspend fun getUsersByProjectId(projectId: UUID): List<User> = tryToExecute(
        function = { userDataSource.getUsersByProjectId(projectId.toString()) },
        onSuccess = { listOfUsers -> listOfUsers.mapNotNull { user -> user.mapToUserEntity() } },
        onFailure = { throw UsersDoesNotExistException() }
    )


    override suspend fun addUserToProject(projectId: UUID, username: String): Boolean = tryToExecute(
        function = { userDataSource.addUserToProject(projectId.toString(), username) },
        onSuccess = { isAddedToProject -> isAddedToProject },
        onFailure = { throw UserNotAddedToProjectException() }
    )


    override suspend fun deleteUserFromProject(projectId: UUID, username: String): Boolean = tryToExecute(
        function = { userDataSource.deleteUserFromProject(projectId.toString(), username) },
        onSuccess = { isDeletedFormProject -> isDeletedFormProject },
        onFailure = { throw UserNotDeletedFromProjectException() }
    )


    override suspend fun addUserToTask(username: String, taskId: UUID): Boolean = tryToExecute(
        function = { userDataSource.addUserToTask(username, taskId.toString()) },
        onSuccess = { isAddedToTask -> isAddedToTask },
        onFailure = { throw UserNotAddedToTaskException() }
    )

    override suspend fun deleteUserFromTask(username: String, taskId: UUID): Boolean = tryToExecute(
        function = { userDataSource.deleteUserFromTask(username, taskId.toString()) },
        onSuccess = { isDeletedFormTask -> isDeletedFormTask },
        onFailure = { throw UserNotDeletedFromTaskException() }
    )

    override suspend fun getUserByUsername(username: String): User =
        tryToExecute(
            function = { userDataSource.getUserByUsername(username) },
            onSuccess = { user -> user?.mapToUserEntity() ?: throw UserDoesNotExistException() },
            onFailure = { throw UserDoesNotExistException() }
        )


    override suspend fun loginUser(username: String, password: String): User =
        tryToExecute(
            function = { userDataSource.getUserByUsername(username) },
            onSuccess = { user ->
                if (user == null) throw UserDoesNotExistException()
                if (hashingService.verify(password, user.hashedPassword)) {
                    currentUserDataSource.setCurrentUser(user)
                    user.mapToUserEntity() ?: throw UserDoesNotExistException()
                } else throw IncorrectPasswordException()
            },
            onFailure = { throw UserDoesNotExistException() }
        )

    override fun getCurrentUser(): User? = currentUserDataSource.getCurrentUser()?.mapToUserEntity()


    override fun logoutUser() = currentUserDataSource.clearCurrentUser()

}