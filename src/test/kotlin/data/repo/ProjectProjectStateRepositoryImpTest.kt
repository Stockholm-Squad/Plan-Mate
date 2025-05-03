package data.repo

import com.google.common.truth.Truth.assertThat
import createState
import io.mockk.every
import io.mockk.mockk
import logic.model.entities.ProjectState
import org.example.data.datasources.PlanMateDataSource
import org.example.data.repo.ProjectStateRepositoryImp
import org.example.logic.model.exceptions.PlanMateExceptions.DataException
import org.example.logic.repository.ProjectStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProjectProjectStateRepositoryImpTest {

    private lateinit var projectStateDataSource: PlanMateDataSource<ProjectState>
    private lateinit var stateRepository: ProjectStateRepository
    private lateinit var projectState: ProjectState

    @BeforeEach
    fun setUp() {
        projectStateDataSource = mockk(relaxed = true)
        projectState = ProjectState(id = "123", name = "In-Progress")
        every { projectStateDataSource.read() } returns Result.success(listOf())
        stateRepository = ProjectStateRepositoryImp(projectStateDataSource)
    }

    @Test
    fun `editState() should return success result when the state not found`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.success(
            value = true
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectState))

        //When
        val result = stateRepository.editProjectState(ProjectState("235", ""))

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return success result with true when the state updated successfully`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.success(
            value = true
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectState))

        //When
        val result = stateRepository.editProjectState(projectState)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return failure result with empty data when file not found`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.success(
            value = true
        )
        every { projectStateDataSource.read() } returns Result.failure(DataException.FileNotExistException())

        //When
        val result = stateRepository.editProjectState(projectState)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with throwable when error happens while write failed`() {
        //Given
        every { projectStateDataSource.read() } returns Result.success(listOf(projectState))
        every { projectStateDataSource.overWrite(any()) } returns Result.failure(
            Throwable()
        )

        //When
        val result = stateRepository.editProjectState(projectState)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return success result with true when state deleted successfully`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.success(
            true
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectState))

        //When
        val result = stateRepository.deleteProjectState(projectState)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteState() should return failure result with throwable when error happens while writing or reading from the csv file`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.failure(
            Throwable()
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectState))

        //When
        val result = stateRepository.deleteProjectState(projectState)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when state not exist`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.failure(
            Throwable()
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectState))

        //When
        val result = stateRepository.deleteProjectState(projectState)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `deleteState() should return failure result with throwable when read returns failure`() {
        //Given
        every { projectStateDataSource.read() } returns Result.failure(Throwable())

        //When
        val result = stateRepository.deleteProjectState(projectState)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `addState() should return success result with true when the state add successfully`() {
        //Given
        val projectState = ProjectState(id = "1", name = "Done")
        every { projectStateDataSource.append(any()) } returns Result.success(true)

        //When
        val result = stateRepository.addProjectState(projectState.name)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with throwable when error happens while writing into the csv file`() {
        //Given
        val projectState = ProjectState(id = "123", name = "In Review")
        every { projectStateDataSource.append(any()) } returns Result.failure(
            Throwable()
        )
        //When
        val result = stateRepository.addProjectState(projectState.name)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `getAllStates() should return success result with list of state when the file have data`() {
        //Given
        val stateList = listOf(
            createState(id = "123", name = "In-progress"),
            createState(id = "13", name = "done"),
            createState(id = "10", name = "in review")
        )
        every { projectStateDataSource.read() } returns Result.success(
            stateList
        )

        //When
        val result = stateRepository.getAllProjectStates()

        //Then
        assertThat(result.getOrThrow()).isEqualTo(stateList)

    }

    @Test
    fun `getAllStates() should return failure result with empty data exception when the file is empty`() {
        //Given
        every { projectStateDataSource.read() } returns Result.success(
            listOf()
        )

        // When
        val result = stateRepository.getAllProjectStates()

        //Then
        assertThat(result.getOrNull()).isEqualTo(listOf<ProjectState>())
    }

    @Test
    fun `getAllStates() should return failure result with throwable when error happens while reading from data`() {
        //Given
        every { projectStateDataSource.read() } returns Result.failure(
            Throwable()
        )

        // When
        val result = stateRepository.getAllProjectStates()

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

}