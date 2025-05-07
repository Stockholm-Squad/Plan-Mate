package logic.models.exceptions

open class DataException(override val message: String) : Exception(message) {
    class ReadDataException(message: String = "Error while reading data!") : DataException(message)
    class WriteDataException(message: String = "Error while writing data!") : DataException(message)
    class EmptyDataException(message: String = "No data available") : DataException(message)
    class FileNotExistException(message: String = "Error file not found!") : DataException(message)
    class NoObjectFound(message: String = "The file is empty") : DataException(message)
}

open class LogicExceptions(override val message: String) : Exception(message)

open class UserExceptions(message: String) : LogicExceptions(message) {
    class InvalidUserNameException(message: String = "Invalid username") : UserExceptions(message)
    class InvalidPasswordException(message: String = "Invalid password") : UserExceptions(message)
    class IncorrectPasswordException(message: String = "Incorrect password") : UserExceptions(message)
    class UserDoesNotExistException(message: String = "User does not exist") : UserExceptions(message)
    class UserExistException(message: String = "User already exists") : UserExceptions(message)
    class UsersDataAreEmptyException(message: String = "Users data are empty") : UserExceptions(message)
}

open class TaskExceptions(message: String) : LogicExceptions(message) {
    class TasksNotFoundException(message: String = "No tasks found.") : TaskExceptions(message)
    class TaskNotFoundException(message: String = "Task Not found.") : TaskExceptions(message)
    class TaskNotAddedException(message: String = "Failed to add task.") : TaskExceptions(message)
    class TaskNotEditException(message: String = "Failed to edit task.") : TaskExceptions(message)
    class TaskNotDeletedException(message: String = "Failed to delete task.") : TaskExceptions(message)
}

open class ProjectExceptions(message: String) : LogicExceptions(message) {
    class ProjectNotFoundException(message: String = "Project not found.") : ProjectExceptions(message)
    class ProjectNotAddedException(message: String = "Project not added.") : ProjectExceptions(message)
    class ProjectNotEditedException(message: String = "Project not edited.") : ProjectExceptions(message)
    class ProjectNotDeletedException(message: String = "Project not deleted.") : ProjectExceptions(message)
    class NoProjectAddedException(message: String = "No project added.") : ProjectExceptions(message)
    class NoProjectsFoundException(message: String = "No projects found.") : ProjectExceptions(message)
}

open class StateExceptions(message: String) : LogicExceptions(message) {
    class StateNotExistException(message: String = "The state does not exist.") : StateExceptions(message)
    class StateAlreadyExistException(message: String = "The state already exists.") : StateExceptions(message)
    class StateNameLengthException(message: String = "The state name is too long.") : StateExceptions(message)
    class NoStatesFoundedException(message: String = "There are no states.") : StateExceptions(message)
    class NotAllowedStateNameException(message: String = "Only letters are allowed!") : StateExceptions(message)
}

open class AuditExceptions(message: String) : LogicExceptions(message) {
    class NoAuditsFoundedException(message: String = "There are no audits.") : AuditExceptions(message)
    class AuditSystemNotAddedException(message: String = "Audit system not added.") : AuditExceptions(message)
}


