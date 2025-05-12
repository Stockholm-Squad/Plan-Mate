package org.example.logic

open class DataException(override val message: String) : Exception(message)
class ReadDataException(message: String = "Error while reading data!") : DataException(message)


open class PlanMateExceptions(override val message: String) : Exception(message)

open class UserExceptions(message: String) : PlanMateExceptions(message)
class InvalidUserNameException(message: String = "Invalid username") : UserExceptions(message)
class InvalidPasswordException(message: String = "Invalid password") : UserExceptions(message)
class IncorrectPasswordException(message: String = "Incorrect password") : UserExceptions(message)
class UserDoesNotExistException(message: String = "User does not exist") : UserExceptions(message)
class UsersDoesNotExistException(message: String = "Users does not exist") : UserExceptions(message)
class UserExistException(message: String = "User already exists") : UserExceptions(message)
class UsersDataAreEmptyException(message: String = "Users data are empty") : UserExceptions(message)
class UserNotAddedException(message: String = "User not added") : UserExceptions(message)


open class TaskExceptions(message: String) : PlanMateExceptions(message)
class TasksNotFoundException(message: String = "No tasks found.") : TaskExceptions(message)
class TaskNotFoundException(message: String = "Task Not found.") : TaskExceptions(message)
class TaskNotAddedException(message: String = "Failed to add task.") : TaskExceptions(message)
class TaskNotUpdatedException(message: String = "Failed to update task.") : TaskExceptions(message)
class TaskNotDeletedException(message: String = "Failed to delete task.") : TaskExceptions(message)
class DuplicateTaskNameException(message: String = "Duplicated task name.") : TaskExceptions(message)


open class ProjectExceptions(message: String) : PlanMateExceptions(message)
class ProjectNotFoundException(message: String = "Project not found.") : ProjectExceptions(message)
class NoProjectAddedException(message: String = "No project added.") : ProjectExceptions(message)
class NoProjectsFoundException(message: String = "No projects found.") : ProjectExceptions(message)
class NoProjectEditedException(message: String = "The project hasn't edited") : ProjectExceptions(message)
class NoProjectDeletedException(message: String = "The project hasn't deleted") : ProjectExceptions(message)
class ProjectAlreadyExistException(message: String = "The project is already exists") : ProjectExceptions(message)


open class EntityStateExceptions(message: String) : PlanMateExceptions(message)
class EntityStateAlreadyExistException(message: String = "The state already exists.") : EntityStateExceptions(message)
class NoEntityStatesFoundedException(message: String = "There are no states.") : EntityStateExceptions(message)
class NotAllowedEntityStateNameException(message: String = "Only letters are allowed!") : EntityStateExceptions(message)
class EntityStateNotAddedException(message: String = "Entity State Not Added") : EntityStateExceptions(message)
class EntityStateNotEditedException(message: String = "Entity State Not Edited") : EntityStateExceptions(message)
class EntityStateNotDeletedException(message: String = "Entity State Not Deleted") :
    EntityStateExceptions(message)

class NoEntityStateFoundException(message: String = "No Entity State Found") : EntityStateExceptions(message)


open class AuditExceptions(message: String) : PlanMateExceptions(message)
class NoAuditsFoundException(message: String = "There are no audits.") : AuditExceptions(message)
class AuditNotAddedException(message: String = "Audit not added.") : AuditExceptions(message)


open class UserToProjectExceptions(message: String) : PlanMateExceptions(message)
class UserNotAddedToProjectException(message: String = "User not added to project") :
    UserToProjectExceptions(message)

class UserNotDeletedFromProjectException(message: String = "User not deleted from project") :
    UserToProjectExceptions(message)


open class UserToTaskExceptions(message: String) : PlanMateExceptions(message)
class UserNotAddedToTaskException(message: String = "User not added to task") :
    UserToTaskExceptions(message)

class UserNotDeletedFromTaskException(message: String = "User not deleted from task") :
    UserToTaskExceptions(message)



