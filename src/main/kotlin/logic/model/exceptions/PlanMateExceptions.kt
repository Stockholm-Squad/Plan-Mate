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

        data class NoObjectFound(override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_DATA_IN_THE_FILE) :
            DataException(exceptionMessage)

        data class NoProjectAdded(override val exceptionMessage: ExceptionMessage = ExceptionMessage.NO_PROJECT_ADDED) :
            DataException(exceptionMessage)

        data class DidNotUpdateProject(override val exceptionMessage: ExceptionMessage = ExceptionMessage.COULD_NOT_UPDATE_PROJECT) :
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