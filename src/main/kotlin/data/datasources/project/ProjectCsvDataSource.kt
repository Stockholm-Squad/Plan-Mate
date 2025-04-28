package org.example.data.datasources.project

import logic.model.entities.Project
import java.io.File
import java.io.FileNotFoundException

class ProjectCsvDataSource(private val file: File) : ProjectDataSource {

    override fun getProjectById(id: String): Result<Project> {
        return try {
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("File does not exist: ${file.absolutePath}")) //TODO: custom Exception
            }

            // Read all lines from the file
            file.readLines().mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size == 3 && parts[0] == id) {
                    Project(parts[0], parts[1], parts[2])
                } else {
                    null
                }
            }.let { projects ->
                // Find the project with the matching ID
                projects.firstOrNull().let { project ->

                    if (project != null) {
                        Result.success(project)
                    } else {
                        Result.failure(Exception("Project not found"))
                    }
                }
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override fun addProject(project: Project): Result<Boolean> {
        return try {
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("File does not exist: ${file.absolutePath}")) //TODO: custom Exception
            }

            file.appendText("${project.id},${project.name},${project.stateId}\n")
            Result.success(true)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override fun editProject(project: Project): Result<Boolean> {
        return try {
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("File does not exist: ${file.absolutePath}")) //TODO: custom Exception
            }

            var exist = false;
            file.readLines().also { lines ->
                lines.map { line ->
                    val parts = line.split(",")
                    if (parts.size == 3 && parts[0] == project.id) {
                        exist = true
                        "${project.id},${project.name},${project.stateId}"
                    } else {
                        line // Keep other lines unchanged
                    }
                }.also { updatedLines ->
                    if (!exist) {
                        return Result.failure(Exception("Project not found")) // TODO: custom expression
                    }

                    // Write the updated lines back to the file
                    file.writeText(updatedLines.joinToString("\n"))
                }
            }
            Result.success(true)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override fun deleteProject(id: String): Result<Boolean> {
        return try {
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("File does not exist: ${file.absolutePath}")) //TODO: custom Exception
            }

            file.readLines().also { lines ->
                lines.filter { line ->
                    val parts = line.split(",")
                    parts.size != 3 || parts[0] != id // Keep lines that don't match the ID
                }.also { filteredLines ->

                    if (filteredLines.size == lines.size) {
                        return Result.failure(Exception("Project not found")) // TODO: Custom Exception
                    }

                    // Write the filtered lines back to the file
                    file.writeText(filteredLines.joinToString("\n"))
                }
            }
            Result.success(true)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    override fun getAllProjects(): Result<List<Project>> {
        return try {
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("File does not exist: ${file.absolutePath}")) //TODO: custom Exception
            }

            // Read all lines from the file and parse them into Project objects
            file.readLines().mapNotNull { line ->
                val parts = line.split(",")
                if (parts.size == 3) {
                    Project(parts[0], parts[1], parts[2])
                } else {
                    null
                }
            }.let { projects ->
                Result.success(projects)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}