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
    }

    sealed class LogicException(
        open val exceptionMessage: ExceptionMessage,
    ) : PlanMateExceptions(exceptionMessage) {
        data class NotAllowedStateNameException(
            override val exceptionMessage: ExceptionMessage = ExceptionMessage.NOT_ALLOWED_STATE_NAME_MESSAGE,
        ) : LogicException(
            exceptionMessage
        )

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
    }
}