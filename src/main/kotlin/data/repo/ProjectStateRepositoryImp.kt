package org.example.data.repo

import logic.model.entities.ProjectState
import org.example.data.datasources.state_data_source.IStateDataSource
import org.example.data.mapper.StateMapper
import org.example.logic.repository.ProjectStateRepository

class ProjectStateRepositoryImp(
    private val stateDataSource: IStateDataSource,
    private val stateMapper: StateMapper,
) : ProjectStateRepository {

    override fun addProjectState(stateName: String): Result<Boolean> {
        return stateDataSource.append(listOf(stateMapper.mapToStateModel(ProjectState(name = stateName))))
    }

    override fun editProjectState(projectState: ProjectState): Result<Boolean> {
        return stateDataSource.read().fold(
            onSuccess = { currentStates ->
                currentStates.map { item -> if (item.id == projectState.id.toString()) projectState else item }
                stateDataSource.overWrite(currentStates)
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun deleteProjectState(projectState: ProjectState): Result<Boolean> {
        return stateDataSource.read().fold(
            onSuccess = { currentStates ->
                currentStates.filterNot { it == stateMapper.mapToStateModel(projectState) }
                stateDataSource.overWrite(currentStates)
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun getAllProjectStates(): Result<List<ProjectState>> {
        return stateDataSource.read().fold(
            onSuccess = { allStates ->
                Result.success(allStates.map { it1 -> stateMapper.mapToStateEntity(it1) })
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

}