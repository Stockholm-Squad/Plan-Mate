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
    }

    sealed class LogicException(
        open val exceptionMessage: ExceptionMessage
    ) : PlanMateExceptions(exceptionMessage) {
        data class InvalidUserName(override val exceptionMessage: ExceptionMessage = ExceptionMessage.INVALID_USER_NAME) :
            LogicException(exceptionMessage)

        data class InvalidPassword(override val exceptionMessage: ExceptionMessage = ExceptionMessage.INVALID_PASSWORD) :
            LogicException(exceptionMessage)

        data class UserDoesNotExist(override val exceptionMessage: ExceptionMessage = ExceptionMessage.USER_DOES_NOT_EXIST) :
            LogicException(exceptionMessage)

        data class IncorrectPassword(override val exceptionMessage: ExceptionMessage = ExceptionMessage.INCORRECT_PASSWORD) :
            LogicException(exceptionMessage)
    }
}