package org.example.data.repo

import org.example.data.mapper.mapToStateEntity
import org.example.data.mapper.mapToStateModel
import org.example.data.source.StateDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.*
import org.example.logic.entities.ProjectState
import org.example.logic.repository.ProjectStateRepository
import java.util.*

class ProjectStateRepositoryImp(
    private val stateDataSource: StateDataSource,
) : ProjectStateRepository {

    override suspend fun addProjectState(projectState: ProjectState): Boolean {
        return tryToExecute(
            { stateDataSource.addProjectState(projectState.mapToStateModel()) },
            onSuccess = { it },
            onFailure = { throw ProjectStateNotAddedException() }
        )
    }

    override suspend fun editProjectState(projectState: ProjectState): Boolean {
        return tryToExecute(
            { stateDataSource.editProjectState(projectState.mapToStateModel()) },
            onSuccess = { it },
            onFailure = { throw ProjectStateNotEditedException() }
        )
    }

    override suspend fun deleteProjectState(projectState: ProjectState): Boolean {
        return tryToExecute(
            { stateDataSource.deleteProjectState(projectState.mapToStateModel()) },
            onSuccess = { it },
            onFailure = { throw ProjectStateNotDeletedException() }
        )

    }

    override suspend fun isProjectStateExist(stateName: String): Boolean {
        return tryToExecute(
            { stateDataSource.isProjectStateExist(stateName) },
            onSuccess = { it },
            onFailure = { throw NoProjectStateFoundException() }
        )
    }

    override suspend fun getAllProjectStates(): List<ProjectState> {
        return tryToExecute(
            { stateDataSource.getAllProjectStates() },
            onSuccess = { it },
            onFailure = { throw NoStatesFoundedException() }
        ).mapNotNull { it.mapToStateEntity() }
    }

    override suspend fun getProjectStateByName(stateName: String): ProjectState {
        return tryToExecute(
            { stateDataSource.getProjectStateByName(stateName) },
            onSuccess = { it },
            onFailure = { throw NoProjectStateFoundException() }
        )?.mapToStateEntity() ?: throw NoProjectStateFoundException()
    }

    override suspend fun getProjectStateByID(stateId: UUID): ProjectState {
        return tryToExecute(
            { stateDataSource.getProjectStateById(stateId.toString()) },
            onSuccess = { it },
            onFailure = { throw NoProjectStateFoundException() }
        )?.mapToStateEntity() ?: throw NoProjectStateFoundException()
    }
}