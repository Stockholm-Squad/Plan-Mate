package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import logic.models.entities.ProjectState
import logic.models.exceptions.*
import org.example.logic.repository.ProjectStateRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ManageStatesUseCaseTest {

    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var manageStatesUseCase: ManageStatesUseCase

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk(relaxed = true)
        manageStatesUseCase = ManageStatesUseCase(projectStateRepository)
    }

    @Test
    fun `editState() should return success result with true when the state name is valid and repo returned success result of true`() {
        //Given
        val stateName = "do"
        val newStateName = "new"
        every { projectStateRepository.getAllProjectStates() } returns Result.success(
            listOf(
                ProjectState(
                    UUID.randomUUID(),
                    stateName
                )
            )
        )
        every { projectStateRepository.editProjectState(any()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

//    @Test
//    fun `editState() should return success result with true when the name of state have leading and trailing space and repo returned success result of true`() {
//        //Given
//        val stateName = "ToDo"
//        val newStateName="New ToDo"
//        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name= "ToDo")))
//        every { manageStatesUseCase.editProjectStateByName(stateName,newStateName) } returns Result.success(true)
//
//        //When
//        val result = manageStatesUseCase.editProjectStateByName(stateName,newStateName)
//
//        //Then
//        assertThat(result.getOrNull()).isEqualTo(true)
//    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given
        val stateName = "#In Review$"
        val newStateName = "New ToDo"

        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with exception when edit state failes`() {
        //Given
        val stateName = "In Review"
        val newStateName = "New ToDo"

        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = stateName)))
        every { projectStateRepository.editProjectState(any()) } returns Result.failure(Throwable())

        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with exception when edit states fails with file not found`() {
        //Given
        val stateName = "In Review"
        val newStateName = "New ToDo"

        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = stateName)))
        every { projectStateRepository.editProjectState(any()) } returns Result.failure(FileNotExistException())

        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThrows<StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val stateName = "1In Rev3ew"
        val newStateName = "New ToDo"

        every { projectStateRepository.editProjectState(any()) } returns Result.failure(NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val stateName = "   "
        val newStateName = "New ToDo"
        every { projectStateRepository.editProjectState(any()) } returns Result.failure(NotAllowedStateNameException())

        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with state not exist exception when state is not exist`() {
        //Given
        val stateName = "TODO"
        val newStateName = "New ToDo"

        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = "tyyyg")))

        //When
        val result = manageStatesUseCase.editProjectStateByName(stateName, newStateName)

        //Then
        assertThrows<StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return success result with true when the state name exist and the repo added successfully`() {
        //Given
        val stateName = "TODO"
        "New ToDo"

        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = stateName)))
        every { projectStateRepository.deleteProjectState(any()) } returns Result.success(true)

        //  When
        val result = manageStatesUseCase.deleteProjectState(stateName)

        //Then
        assertThat(result.getOrThrow()).isEqualTo(true)
    }

    @Test
    fun `deleteState() should return failure result with exception when the state name not valid`() {
        //Given
        val stateName = "TOD&%O"
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = stateName)))
        every { projectStateRepository.deleteProjectState(any()) } returns Result.success(true)

        //  When
        val result = manageStatesUseCase.deleteProjectState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with exception when repo returns failed with file not found exception`() {
        //Given
        val stateName = "TOD&%O"
        every { projectStateRepository.getAllProjectStates() } returns Result.failure(
            FileNotExistException()
        )

        //  When
        val result = manageStatesUseCase.deleteProjectState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when the state name not exist`() {
        //Given
        val stateName = "TODO"
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = "In Progress")))

        //  When
        val result = manageStatesUseCase.deleteProjectState(stateName)

        //Then
        assertThrows<StateNotExistException> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when repo returned failure result while editing`() {
        //Given
        val stateName = "TODO"
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = stateName)))
        every { projectStateRepository.deleteProjectState(any()) } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.deleteProjectState(stateName)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the name of state is valid`() {
        // Given
        val stateName = "ToDo"

        every { projectStateRepository.getAllProjectStates() } returns Result.success(
            listOf(
                ProjectState(name = "Done"),
                ProjectState(name = "doing")
            )
        )
        every { projectStateRepository.addProjectState(stateName) } returns Result.success(true)

        // When
        val result = manageStatesUseCase.addProjectState(stateName)

        // Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with throwable when the addState fails`() {
        // Given
        val stateName = "ToDo"
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = "Done")))
        every { projectStateRepository.addProjectState(any()) } returns Result.failure(Throwable())
        // When
        val result = manageStatesUseCase.addProjectState(stateName)
        // Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the name of state have leading and trailing space`() {
        //Given
        val stateName = "   ToDo    "
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = "Doing")))
        every { projectStateRepository.addProjectState(stateName.trim()) } returns Result.success(true)
        //When
        val result = manageStatesUseCase.addProjectState(stateName)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result when the state exist`() {
        //Given
        val stateName = "ToDo"
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(ProjectState(name = stateName)))

        //When
        val result = manageStatesUseCase.addProjectState(stateName)

        //Then
        assertThrows<StateAlreadyExistException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain special characters`() {
        //Given
        val stateName = "#I Review$"
        every { projectStateRepository.addProjectState(any()) } returns Result.failure(Throwable())
        //When
        val result = manageStatesUseCase.addProjectState(stateName)
        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name of state contain number`() {
        //Given
        val stateName = "1I Rev3ew"
        every { projectStateRepository.addProjectState(stateName) } returns Result.failure(NotAllowedStateNameException())
        //When
        val result = manageStatesUseCase.addProjectState(stateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return failure result with not allowed state name exception when the name is blank string`() {
        //Given
        val stateName = "   "
        every { projectStateRepository.addProjectState(any()) } returns Result.success(true)

        //When
        val result = manageStatesUseCase.addProjectState(stateName)

        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test


    fun `addState() should return failure result with not allowed length exception when the name of state is more than 30`() {
        //Given
        val state = "hi in this state this is too long state"
        every { projectStateRepository.addProjectState(state) } returns Result.failure(StateNameLengthException())
        //When
        val result = manageStatesUseCase.addProjectState(state)
        //Then
        assertThrows<NotAllowedStateNameException> { result.getOrThrow() }
    }

    @Test

    fun `getAllStates() should return success result with list of state when the file have data`() {
        //Given
        val state = listOf(
            ProjectState(name = "todo")
        )
        every { projectStateRepository.getAllProjectStates() } returns Result.success(state)
        //  When
        val result = manageStatesUseCase.getAllProjectStates()

        //Then
        assertThat(result.getOrThrow()).isEqualTo(state)
    }

    @Test
    fun `getAllStates() should return failure result with empty data exception when have no data`() {
        //Given & When
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf())
        //  When
        val result = manageStatesUseCase.getAllProjectStates()

        //Then
        assertThrows<EmptyDataException> { result.getOrThrow() }
    }

    @Test
    fun `getAllStates() should return failure result with read exception when error happens while reading from data`() {
        //Given
        every { projectStateRepository.getAllProjectStates() } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.getAllProjectStates()

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `getStateIdByName() should return id of state when state exist`() {
        //Given
        val projectState = ProjectState(name = "TODO")
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(projectState))

        //  When
        val result = manageStatesUseCase.getProjectStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(projectState.id)
    }

    @Test
    fun `getStateIdByName() should return null when state not exist`() {
        //Given
        val projectState = ProjectState(name = "TODO")
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf(projectState))

        //  When
        val result = manageStatesUseCase.getProjectStateIdByName("injhb")

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when state name not valid`() {
        //Given
        val projectState = ProjectState(name = "T&^^ODO")
        every { projectStateRepository.getAllProjectStates() } returns Result.success(listOf())

        //  When
        val result = manageStatesUseCase.getProjectStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `getStateIdByName() should return null when getAllState return failure`() {
        //Given
        val projectState = ProjectState(name = "TODO")
        every { projectStateRepository.getAllProjectStates() } returns Result.failure(Throwable())

        //  When
        val result = manageStatesUseCase.getProjectStateIdByName(projectState.name)

        //Then
        assertThat(result).isEqualTo(null)
    }

}
