package org.example.data.repo

import logic.models.entities.ProjectState
import logic.models.exceptions.StateExceptions
import org.example.data.datasources.state_data_source.IStateDataSource
import org.example.data.mapper.mapToStateEntity
import org.example.data.mapper.mapToStateModel
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.repository.ProjectStateRepository

class ProjectStateRepositoryImp(
    private val stateDataSource: IStateDataSource,
) : ProjectStateRepository {

    override suspend fun addProjectState(stateName: String): Boolean {
        ProjectState(name = stateName).also { projectState ->
            return executeSafelyWithContext(
                onSuccess = { stateDataSource.append(listOf((projectState.mapToStateModel()))) },
                onFailure = { throw StateExceptions.ProjectStateNotAddedException() }
            )
        }
    }

    override suspend fun editProjectState(projectState: ProjectState): Boolean {
        return executeSafelyWithContext(
            onSuccess = {
                stateDataSource.read()
                    .let { currentStates ->
                        val updatedStates = currentStates.map { item ->
                            if (item.id == projectState.id.toString()) projectState.mapToStateModel() else item
                        }
                        stateDataSource.overWrite(updatedStates)
                    }
            },
            onFailure = { throw StateExceptions.ProjectStateNotEditedException() }
        )
    }

    override suspend fun deleteProjectState(projectState: ProjectState): Boolean {
        return executeSafelyWithContext(
            onSuccess = {
                stateDataSource.read().let { currentStates ->
                    val updatedStates = currentStates.filterNot { it.id == projectState.id.toString() }
                    stateDataSource.overWrite(updatedStates)
                }
            },
            onFailure = {
                throw StateExceptions.ProjectStateNotDeletedException()
            }
        )
    }

    override suspend fun getAllProjectStates(): List<ProjectState> {
        return executeSafelyWithContext(
            onSuccess = {
                stateDataSource.read().let { allStates ->
                    allStates.mapNotNull { it.mapToStateEntity() }
                }
            },
            onFailure = { throw StateExceptions.NoProjectStateFoundException() }
        )
    }
}