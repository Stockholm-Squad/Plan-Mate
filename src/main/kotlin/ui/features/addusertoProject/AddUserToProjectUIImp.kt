import logic.model.entities.User
import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.project.ManageUsersAssignedToProjectUseCase
import org.example.ui.features.addusertoProject.AddUserToProjectUI
import org.example.ui.features.user.CreateUserUi
import org.example.ui.input_output.input.InputReader
import org.example.ui.input_output.output.OutputPrinter

class AddUserToProjectUIImp(
    private val inputReader: InputReader,
    private val outputPrinter: OutputPrinter,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageUsersAssignedToProjectUseCase: ManageUsersAssignedToProjectUseCase,
    private val createUserUiImp: CreateUserUi,
    private val authenticationUseCase: LoginUseCase
) : AddUserToProjectUI {

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
        while (true) {
            outputPrinter.showMessage("Would you like to add a new user first? (yes/no): ")
            if (inputReader.readStringOrNull().equals("yes", ignoreCase = true)) {
                createUserUiImp.launchUi(user)
            }

            outputPrinter.showMessage("Enter username to assign or leave it blank to back: ")
            val username = inputReader.readStringOrNull() ?: return
            if (username.equals("done", ignoreCase = true)) break

            outputPrinter.showMessage("Enter project Name: ")
            val projectName = inputReader.readStringOrNull() ?: continue

            assignUserToProject(username, projectName)
        }
    }

    private fun assignUserToProject(username: String, projectName: String) {


        authenticationUseCase.isUserExists(username).fold(
            onSuccess = {
                if (!it) {
                    outputPrinter.showMessage("User does not exist")
                    return
                }
            },
            onFailure = {
                outputPrinter.showMessage("User does not exist")
                return
            }
        )

        manageProjectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                manageUsersAssignedToProjectUseCase.addUserToProject(project.id, username).fold(
                    onSuccess = { success ->
                        if (success) {
                            outputPrinter.showMessage("User assigned successfully")
                        } else {
                            outputPrinter.showMessage("Failed to assign user to project")
                        }
                    },
                    onFailure = { e ->
                        outputPrinter.showMessage(e.message ?: "Failed to assign user to project")
                    }
                )
            },
            onFailure = {
                outputPrinter.showMessage("Project does not exist")
            }
        )
    }

    override fun showUsersAssignedToProject() {
        outputPrinter.showMessage("Enter project name to view assigned users (leave blank to cancel): ")
        val projectName = inputReader.readStringOrNull() ?: return

        manageProjectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                manageUsersAssignedToProjectUseCase.getUsersByProjectId(project.id).fold(
                    onSuccess = { users ->
                        if (users.isEmpty()) {
                            outputPrinter.showMessage("No users assigned to this project")
                        } else {
                            outputPrinter.showMessage("Users assigned to project '$projectName':")
                            users.forEachIndexed { index, user ->
                                outputPrinter.showMessage("${index + 1}. ${user.username}")
                            }
                        }
                    },
                    onFailure = { e ->
                        outputPrinter.showMessage(e.message ?: "Failed to get assigned users")
                    }
                )
            },
            onFailure = { e ->
                outputPrinter.showMessage(e.message ?: "Project not found")
            }
        )
    }

    override fun removeUserFromProject() {
        outputPrinter.showMessage("Enter project name (leave blank to cancel): ")
        val projectName = inputReader.readStringOrNull() ?: return


        manageProjectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                outputPrinter.showMessage("Enter username to remove from project (leave blank to cancel): ")
                val username = inputReader.readStringOrNull() ?: return


                authenticationUseCase.isUserExists(username).fold(
                    onSuccess = {
                        if (!it) {
                            outputPrinter.showMessage("User does not exist")
                            return
                        }
                    },
                    onFailure = {
                        outputPrinter.showMessage("User does not exist")
                        return
                    }
                )

                manageUsersAssignedToProjectUseCase.deleteUserFromProject(project.id, username).fold(
                    onSuccess = { success ->
                        if (success) {
                            outputPrinter.showMessage("User '$username' removed from project '$projectName' successfully")
                        } else {
                            outputPrinter.showMessage("Failed to remove user from project")
                        }
                    },
                    onFailure = { e ->
                        outputPrinter.run { showMessage(e.message ?: "Failed to remove user from project") }
                    }
                )
            },
            onFailure = { e ->
                outputPrinter.showMessage(e.message ?: "Project not found")
            }
        )
    }

}