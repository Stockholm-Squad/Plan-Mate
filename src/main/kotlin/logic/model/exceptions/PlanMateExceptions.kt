package org.example.logic.model.exceptions

sealed class PlanMateExceptions(
    exceptionMessage: ExceptionMessage
) : Throwable(message = exceptionMessage.message) {
    sealed class DataException(open val exceptionMessage: ExceptionMessage) : PlanMateExceptions(exceptionMessage) {
        data class ReadException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.READ_EXCEPTION_MESSAGE
        ) : DataException(exceptionMessage)

        data class WriteException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.WRITE_EXCEPTION_MESSAGE
        ) : DataException(exceptionMessage)

        data class FileNotExistException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.FILE_NOT_EXIST_EXCEPTION_MESSAGE
        ) : DataException(exceptionMessage)
    }

    sealed class LogicException(
        open val exceptionMessage: ExceptionMessage
    ) : PlanMateExceptions(exceptionMessage) {
        data class NoTasksFound(override val exceptionMessage: ExceptionMessage= ExceptionMessage.NO_TASKS_FOUNDED) : LogicException(exceptionMessage)
        data class NoTasksCreated(override val exceptionMessage: ExceptionMessage= ExceptionMessage.FAILED_TO_CREATE_TASK) : LogicException(exceptionMessage)
        data class NoTasksDeleted(override val exceptionMessage: ExceptionMessage= ExceptionMessage.FAILED_TO_DELETE_TASK) : LogicException(exceptionMessage)

        data class TaskAlreadyExistsException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.TASK_ALREADY_EXISTS
        ) : LogicException(exceptionMessage)

//        data class InvalidTaskDataException(
//            override val exceptionMessage: ExceptionMessage = ExceptionMessage.INVALID_TASK_DATA
//        ) : LogicException(exceptionMessage)
//
        data class TaskNotFoundException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.TASK_NOT_FOUND
        ) : LogicException(exceptionMessage)
    }
    }

