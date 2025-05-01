package org.example.logic.model.exceptions

sealed class PlanMateExceptions(
    exceptionMessage: ExceptionMessage,
) : Throwable(message = exceptionMessage.message) {
    sealed class DataException(open val exceptionMessage: ExceptionMessage) : PlanMateExceptions(exceptionMessage) {

        data class ReadException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.READ_EXCEPTION_MESSAGE,
        ) : DataException(exceptionMessage)

        data class WriteException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.WRITE_EXCEPTION_MESSAGE,
        ) : DataException(exceptionMessage)

        data class EmptyDataException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.EMPTY_DATA_MESSAGE,
        ) : DataException(exceptionMessage)

        data class FileNotExistException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.FILE_NOT_EXIST_EXCEPTION_MESSAGE
        ) : DataException(exceptionMessage)

        data class NoObjectFound(override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_DATA_IN_THE_FILE) :
            DataException(exceptionMessage)

    }

    sealed class LogicException(
        open val exceptionMessage: ExceptionMessage,
    ) : PlanMateExceptions(exceptionMessage) {
        data class NotAllowedStateNameException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.NOT_ALLOWED_STATE_NAME_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )
        data class InvalidPassword(override val exceptionMessage: ExceptionMessage = ExceptionMessage.INVALID_PASSWORD) :
            LogicException(exceptionMessage)


        data class NoProjectAdded(override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_PROJECT_ADDED) :
            DataException(exceptionMessage)

              data class UserDoesNotExist(override val exceptionMessage: ExceptionMessage = ExceptionMessage.USER_DOES_NOT_EXIST) :
            LogicException(exceptionMessage)

        data class IncorrectPassword(override val exceptionMessage: ExceptionMessage = ExceptionMessage.INCORRECT_PASSWORD) :
            LogicException(exceptionMessage)

        data class UserExist(override val exceptionMessage: ExceptionMessage = ExceptionMessage.USER_EXIST) :
            LogicException(exceptionMessage)


        data class UsersIsEmpty(override val exceptionMessage: ExceptionMessage = ExceptionMessage.USERS_IS_EMPTY) :
            LogicException(exceptionMessage)

        data class InvalidUserName(override val exceptionMessage: ExceptionMessage = ExceptionMessage.INVALID_USER_NAME) :
            LogicException(exceptionMessage)

        data class NoTasksFound(override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_TASKS_FOUNDED) :
            LogicException(exceptionMessage)

        data class TaskNotFoundException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_TASKS_FOUND
        ) : LogicException(exceptionMessage)

        data class TaskAlreadyExistsException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.TASK_ALREADY_EXISTS
        ) : LogicException(exceptionMessage)

        data class NoTaskAssignmentFound(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.No_Task_Assignment_Found
        ) : LogicException(exceptionMessage)

        data class NoTasksCreated(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.FAILED_TO_CREATE_TASK
        ) : LogicException(exceptionMessage)

        data class NoTasksDeleted(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.FAILED_TO_DELETE_TASK
        ) : LogicException(exceptionMessage)

        data class DidNotUpdateProject(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.COULD_NOT_UPDATE_PROJECT
        ) : LogicException(exceptionMessage)

        data class ProjectNotFoundException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.PROJECT_NOT_FOUND
        ) : LogicException(exceptionMessage)

        data class NoStatesFound(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_STATES_FOUND
        ) : LogicException(exceptionMessage)

        data class NoObjectFound(override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_DATA_IN_THE_FILE) :
            DataException(exceptionMessage)

        data class StateNotExistException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.STATE_NOT_EXIST_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )

        data class StateAlreadyExistException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.STATE_ALREADY_EXIST_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )

        data class StateNameLengthException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.STATE_NAME_LENGTH_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )

        data class NoStatesFoundedException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_STATE_FOUNDED_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )

        data class InvalidStateName(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.INVALID_STATE_NAME_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )
    }
}