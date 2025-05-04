package org.example.data.repo

import logic.model.entities.ProjectState
import org.example.data.datasources.state_data_source.IStateDataSource
import org.example.data.mapper.mapToStateEntity
import org.example.data.mapper.mapToStateModel
import org.example.logic.repository.ProjectStateRepository

class ProjectStateRepositoryImp(
    private val stateDataSource: IStateDataSource,
) : ProjectStateRepository {

    override fun addProjectState(stateName: String): Result<Boolean> {
        val projectState=ProjectState(name = stateName)
        return stateDataSource.append(listOf((projectState.mapToStateModel())))
    }

    override fun editProjectState(projectState: ProjectState): Result<Boolean> {
        return stateDataSource.read().fold(
            onSuccess = { currentStates ->
                val updatedStates = currentStates.map { item ->
                    if (item.id == projectState.id.toString()) projectState.mapToStateModel() else item
                }
                stateDataSource.overWrite(updatedStates)
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun deleteProjectState(projectState: ProjectState): Result<Boolean> {
        return stateDataSource.read().fold(
            onSuccess = { currentStates ->
                val updatedStates = currentStates.filterNot { it.id == projectState.id.toString() }
                stateDataSource.overWrite(updatedStates)
            },
            onFailure = { Result.failure(it) }
        )
    }
    override fun getAllProjectStates(): Result<List<ProjectState>> {
        return stateDataSource.read().fold(
            onSuccess = { allStates ->
                Result.success(allStates.mapNotNull { it.mapToStateEntity() })
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

}