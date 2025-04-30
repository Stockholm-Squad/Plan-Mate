package ui.features.task

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.Task
import org.example.input_output.output.OutputPrinter
import org.example.logic.model.exceptions.ExceptionMessage
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.ui.features.task.ManageTasksUi
import org.example.utils.DateHandler
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class ManageTasksUiTest {

    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var manageTasksUi: ManageTasksUi
    private val dateHandler = DateHandler()
    private lateinit var printer: OutputPrinter

    @BeforeEach
    fun setUp(){
        manageTasksUseCase= mockk(relaxed = true)
        printer=mockk(relaxed = true)
        manageTasksUi= ManageTasksUi(manageTasksUseCase,printer)
    }
    private val sampleTasks = listOf(
        Task(
            name = "Task 1",
            description = "First task",
            stateId = "1",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        ),
        Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
    )
// showAllTasks
    @Test
    fun`getAllTasks() should return all tasks when use case succeeds`(){
       //Given
        every { manageTasksUseCase.getAllTasks() } returns Result.success(sampleTasks)
        //When
        val result=manageTasksUi.showAllTasks()
        //Then
        verify { printer.showMessage("$sampleTasks") }
    }

    @Test
    fun `should return failure when use case fails`() {
        //Given
        every { manageTasksUseCase.getAllTasks() } returns Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
        //When
        val result = manageTasksUi.showAllTasks()
        //Then
       verify { printer.showMessage("${PlanMateExceptions.LogicException.NoTasksFound().message}")}

    }

    //createTask
    @Test
    fun `createTask() should return true when use case succeeds`(){
        //Given
        val newTask = Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.success(true)

        //When
        val result=manageTasksUi.createTask(newTask)
        //Then
        verify { printer.showMessage("${ExceptionMessage.TASK_CREATED_SUCCESSFULLY}")}

    }
    @Test
    fun `createTask() should return false when use case fails`(){
        //Given
        val newTask = Task(
            name = "Task 2",
            description = "Second task",
            stateId = "2",
            createdDate = dateHandler.getCurrentDateTime(),
            updatedDate = dateHandler.getCurrentDateTime()
        )
        every { manageTasksUseCase.createTask(newTask) } returns Result.failure(PlanMateExceptions.LogicException.NoTasksCreated())

        //When
        val result=manageTasksUi.createTask(newTask)
        //Then
        verify { printer.showMessage("${ExceptionMessage.FAILED_TO_CREATE_TASK}")}

    }


}