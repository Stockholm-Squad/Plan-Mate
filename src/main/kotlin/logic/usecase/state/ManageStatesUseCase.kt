package org.example.logic.usecase.state

import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import logic.model.entities.ProjectState
import org.example.data.utils.DateHandlerImp
import org.example.logic.model.exceptions.*
import org.example.logic.repository.ProjectStateRepository
import org.example.logic.usecase.audit.AddAuditSystemUseCase
import org.example.logic.usecase.extention.isLetterOrWhiteSpace
import org.example.logic.usecase.extention.isValidLength
import java.util.*

class ManageStatesUseCase(
    private val projectStateRepository: ProjectStateRepository,
    private val auditSystemUseCase: AddAuditSystemUseCase,
) {
    fun addProjectState(stateName: String,userId: UUID): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = { validStateName ->
                validStateName.takeIf { state -> !isProjectStateExist(state) }
                    ?.let {
                        val projectState=ProjectState(UUID.randomUUID(),it)
                        projectStateRepository.addProjectState(projectState).fold(
                            onSuccess = { addAuditSystem(projectState.id,userId) },
                            onFailure = { exception -> Result.failure(exception) }
                        )

                    } ?: Result.failure(StateAlreadyExistException())
            },
            onFailure = { Result.failure(NotAllowedStateNameException()) }
        )
    }

    fun addAuditSystem(projectId: UUID,userId:UUID): Result<Boolean> {
        val auditSystem= listOf(AuditSystem(entityType = EntityType.PROJECT, entityTypeId =projectId
            , description = "", dateTime = DateHandlerImp().getCurrentDateTime(), userId = userId))
        return auditSystemUseCase.addAuditsEntries(auditSystem).runCatching { return Result.success(true) }
    }

    fun editProjectStateByName(stateName: String,newStateName:String,userId: UUID): Result<Boolean> {
        return validateStateNames(stateName,newStateName).fold(
            onSuccess = {
                getProjectState(stateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        projectStateRepository.editProjectState(ProjectState(state.id,newStateName)).fold(
                            onSuccess = {addAuditSystem(state.id,userId) },
                            onFailure = { throwable ->
                                this@ManageStatesUseCase.handleProjectStateNotExistException(
                                    throwable
                                )
                            }
                        )
                    } ?: Result.failure(StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    private fun validateStateNames(stateName: String, newStateName: String): Result<Pair<String, String>> {
        return isStateNameValid(stateName).fold(
            onSuccess = { validStateName ->
                isStateNameValid(newStateName).map { validNewName ->
                    validStateName to validNewName
                }
            },
            onFailure = { Result.failure(it) }
        )
    }
    private fun isStateNameValid(stateName: String): Result<String> {
        return stateName.trim().takeIf {
            it.isNotBlank() &&
                    it.isValidLength(30) &&
                    it.isLetterOrWhiteSpace()
        }?.let { Result.success(it) }
            ?: Result.failure(NotAllowedStateNameException())
    }

    fun deleteProjectState(stateName: String,userId: UUID): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                getProjectState(stateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        projectStateRepository.deleteProjectState(state).fold(
                            onSuccess = { addAuditSystem(state.id, userId ) },
                            onFailure = { throwable ->
                                this@ManageStatesUseCase.handleProjectStateNotExistException(
                                    throwable
                                )
                            }
                        )
                    } ?: Result.failure(StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    fun getAllProjectStates(): Result<List<ProjectState>> {
        return projectStateRepository.getAllProjectStates().fold(
            onSuccess = { data ->
                data.takeIf { data.isNotEmpty() }?.let {
                    Result.success(data)
                } ?: Result.failure(EmptyDataException())
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    fun getProjectStateIdByName(stateName: String): UUID? {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                getProjectState(stateName).takeIf { it != null }?.id
            },
            onFailure = { null }
        )
    }

    private fun handleProjectStateNotExistException(throwable: Throwable): Result<Boolean> {
        return throwable.takeIf { it is FileNotExistException }
            ?.let { Result.failure(StateNotExistException()) }
            ?: Result.failure(throwable)
    }


    private fun isProjectStateExist(stateName: String): Boolean {
        return (getProjectState(stateName) != null)
    }

    private fun getProjectState(stateName: String): ProjectState? {
        return this.getAllProjectStates().fold(
            onSuccess = { allStates ->
                allStates.firstOrNull { state -> state.name.equals(stateName, ignoreCase = true) }
            },
            onFailure = { null }
        )
    }

    fun getProjectStateNameByStateId(stateId: UUID): String? {
        return getAllProjectStates()
            .fold(
                onSuccess = { states -> findStateById(states, stateId)?.name },
                onFailure = { null }
            )
    }

    private fun findStateById(states: List<ProjectState>, stateId: UUID): ProjectState? {
        return states.firstOrNull { it.id == stateId }
    }
}