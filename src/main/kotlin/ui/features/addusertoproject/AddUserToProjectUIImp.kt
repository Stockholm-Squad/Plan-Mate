import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import logic.models.entities.User
import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.addusertoProject.AddUserToProjectUI
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class AddUserToProjectUIImp(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase,
    private val createUserUiImp: CreateUserUi,
    private val authenticationUseCase: LoginUseCase
) : AddUserToProjectUI {
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        outputPrinter.showMessage(throwable.message ?: "Unknown error")
    }

    override fun invoke(user: User?) {
        while (true) {
            outputPrinter.showMessage("\nUsers In Project Management:")
            outputPrinter.showMessage("1. Assign users to project")
            outputPrinter.showMessage("2. View users assigned to project")
            outputPrinter.showMessage("3. Remove user from project")
            outputPrinter.showMessage("0. Back")

            when (inputReader.readStringOrNull()) {
                "1" -> assignUsersToProject(user)
                "2" -> showUsersAssignedToProject()
                "3" -> removeUserFromProject()
                "0" -> return
                else -> outputPrinter.showMessage("Invalid choice")
            }
        }
    }

    override fun assignUsersToProject(user: User?) {
        runBlocking(errorHandler) {
            while (true) {
                outputPrinter.showMessage("Would you like to add a new user first? (yes/no): ")
                if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                    createUserUiImp.launchUi(user)
                }

                outputPrinter.showMessage("Enter username to assign or leave it blank to back: ")
                val username = inputReader.readStringOrNull() ?: return@runBlocking
                if (username.equals("done", ignoreCase = true)) break

                outputPrinter.showMessage("Enter project Name: ")
                val projectName = inputReader.readStringOrNull() ?: continue

                assignUserToProject(username, projectName)
            }
        }
    }

    private fun assignUserToProject(username: String, projectName: String) = runBlocking(errorHandler) {
        authenticationUseCase.isUserExists(username).let {
            if (!it) {
                outputPrinter.showMessage("User does not exist")
                return@runBlocking
            }
        }
        getProjectsUseCase.getProjectByName(projectName).let { project ->
            manageUsersAssignedToProjectUseCase.addUserToProject(project.id, username).let {
                if (it) {
                    outputPrinter.showMessage("User assigned successfully")

                } else {
                    outputPrinter.showMessage("Failed to assign user to project")
                }
            }
        }
    }

    override fun showUsersAssignedToProject() {
        outputPrinter.showMessage("Enter project name to view assigned users (leave blank to cancel): ")
        inputReader.readStringOrNull()?.let { input ->
            runBlocking(errorHandler) {
                getProjectsUseCase.getProjectByName(input).let { project ->
                    manageUsersAssignedToProjectUseCase.getUsersByProjectId(project.id).let { users ->
                        if (users.isEmpty()) {
                            outputPrinter.showMessage("No users assigned to this project")

                        } else {
                            outputPrinter.showMessage("Users assigned to project '$input':")
                            users.forEachIndexed { index, user ->
                                outputPrinter.showMessage("${index + 1}. ${user.username}")

                            }
                        }
                    }
                }
            }
        }
    }

    override fun removeUserFromProject() {
        outputPrinter.showMessage("Enter project name (leave blank to cancel): ")
        runBlocking(errorHandler) {
            inputReader.readStringOrNull()?.let { projectName ->
                outputPrinter.showMessage("Enter username to remove from project (leave blank to cancel): ")
                inputReader.readStringOrNull()?.let { username ->
                    manageUsersAssignedToProjectUseCase.deleteUserFromProject(projectName, username)
                }
            }
        }
    }


}