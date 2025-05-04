package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import logic.model.entities.ProjectState
import org.example.data.datasources.state_data_source.IStateDataSource
import org.example.data.models.ProjectStateModel
import org.example.data.repo.ProjectStateRepositoryImp
import org.example.logic.model.exceptions.FileNotExistException
import org.example.logic.repository.ProjectStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProjectStateRepositoryImpTest {

    private lateinit var projectStateDataSource: IStateDataSource
    private lateinit var stateRepository: ProjectStateRepository
    private lateinit var projectState: ProjectState
    private lateinit var projectStateModel: ProjectStateModel


    @BeforeEach
    fun setUp() {
        projectStateDataSource = mockk(relaxed = true)
        projectState = ProjectState(name = "In-Progress")
        every { projectStateDataSource.read() } returns Result.success(listOf())
        stateRepository = ProjectStateRepositoryImp(projectStateDataSource)
        projectStateModel=ProjectStateModel("id","project name")
    }

    @Test
    fun `editState() should return success result when the state not found`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.success(
            value = true
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectStateModel))

        //When
        val result = stateRepository.editProjectState(ProjectState(name = "In-Progress"))

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editState() should return success result with true when the state updated successfully`() {
        //Given
        every { projectStateDataSource.overWrite(any()) } returns Result.success(
            value = true
        )
        every { projectStateDataSource.read() } returns Result.success(listOf(projectStateModel))

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
        every { projectStateDataSource.read() } returns Result.failure(FileNotExistException())

        //When
        val result = stateRepository.editProjectState(projectState)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

    @Test
    fun `editState() should return failure result with throwable when error happens while write failed`() {
        //Given
        every { projectStateDataSource.read() } returns Result.success(listOf(projectStateModel))
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
        every { projectStateDataSource.read() } returns Result.success(listOf(projectStateModel))

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
        every { projectStateDataSource.read() } returns Result.success(listOf(projectStateModel))

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
        every { projectStateDataSource.read() } returns Result.success(listOf(projectStateModel))

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
        val projectState = ProjectState(name = "Done")
        every { projectStateDataSource.append(any()) } returns Result.success(true)

        //When
        val result = stateRepository.addProjectState(projectState.name)

        //Then
        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `addState() should return failure result with throwable when error happens while writing into the csv file`() {
        //Given
        val projectState = ProjectState(name = "In Review")
        every { projectStateDataSource.append(any()) } returns Result.failure(
            Throwable()
        )
        //When
        val result = stateRepository.addProjectState(projectState.name)

        //Then
        assertThrows<Throwable> { result.getOrThrow() }
    }

//    @Test
//    fun `getAllStates should return success result with list of states when file has data`() {
//        // Given
//        val testUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000") // Constant UUID
//        val expectedState = ProjectState(id = testUuid, name = "Done")
//        val expectedList = listOf(expectedState)
//
//        every { stateRepository.getAllProjectStates() } returns Result.success(expectedList)
//
//        // When
//        val result = stateRepository.getAllProjectStates()
//
//        // Then
//        assertThat(result.getOrThrow()).isEqualTo(expectedList)
//    }

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