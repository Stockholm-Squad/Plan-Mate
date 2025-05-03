package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import org.example.logic.usecase.task.ManageTasksUseCase
import org.example.logic.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import org.example.logic.model.exceptions.NoTasksCreated
import org.example.logic.model.exceptions.NoTasksFound
import org.example.logic.model.exceptions.TaskNotFoundException
import org.example.logic.model.exceptions.NoTasksDeleted
import org.example.data.utils.DateHandlerImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.buildTask


class ManageTasksUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var dataHandler: DateHandlerImp

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
        dataHandler = DateHandlerImp()
    }

    @Test
    fun `getAllTasks() should return success result with a list of tasks when the file has data`() {
        val taskList = listOf(
            buildTask(name = "Task 1"),
            buildTask(name = "Task 2")
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        val result = manageTasksUseCase.getAllTasks()

        assertThat(result.getOrNull()).isEqualTo(taskList)
    }

    @Test
    fun `getAllTasks() should return failure result when no tasks are found`() {
        every { taskRepository.getAllTasks() } returns Result.failure(NoTasksFound())

        val result = manageTasksUseCase.getAllTasks()

        assertThrows<NoTasksFound> { result.getOrThrow() }
    }

    @Test
    fun `getTaskByName() should return success result when the task is found`() {
        val taskName = "Task 1"
        val taskList = listOf(
            buildTask(name = taskName),
            buildTask(name = "Task 2")
        )
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        val result = manageTasksUseCase.getTaskByName(taskName)

        assertThat(result.getOrNull()?.name).isEqualTo(taskName)
    }

    @Test
    fun `getTaskByName() should return failure result when the task is not found`() {
        val taskName = "NonExistent"
        val taskList = listOf(buildTask(name = "OtherTask"))
        every { taskRepository.getAllTasks() } returns Result.success(taskList)

        val result = manageTasksUseCase.getTaskByName(taskName)

        assertThrows<TaskNotFoundException> { result.getOrThrow() }
    }

    @Test
    fun `createTask() should return true result when the task is created`() {
        val task = buildTask(name = "NewTask")
        every { taskRepository.addTask(task) } returns Result.success(true)

        val result = manageTasksUseCase.createTask(task)

        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `createTask() should return failure result when task creation fails`() {
        val task = buildTask(name = "DuplicateTask")
        every { taskRepository.addTask(task) } returns Result.failure(NoTasksCreated())

        val result = manageTasksUseCase.createTask(task)

        assertThrows<NoTasksCreated> { result.getOrThrow() }
    }

    @Test
    fun `editTask() should return success result when the task is updated`() {
        val task = buildTask(name = "Task 1")
        every { taskRepository.editTask(task) } returns Result.success(true)

        val result = manageTasksUseCase.editTask(task)

        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `editTask() should return failure result when update fails`() {
        val task = buildTask(name = "Task 1")
        every { taskRepository.editTask(task) } returns Result.failure(NoTasksFound())

        val result = manageTasksUseCase.editTask(task)

        assertThrows<NoTasksFound> { result.getOrThrow() }
    }

    @Test
    fun `deleteTaskByName() should return success result when the task is deleted`() {
        val task = buildTask(name = "Task 1")
        every { taskRepository.getAllTasks() } returns Result.success(listOf(task))
        every { taskRepository.deleteTask(task.id) } returns Result.success(true)

        val result = manageTasksUseCase.deleteTaskByName("Task 1")

        assertThat(result.getOrNull()).isEqualTo(true)
    }

    @Test
    fun `deleteTaskByName() should return failure result when task is not found`() {
        every { taskRepository.getAllTasks() } returns Result.success(emptyList())

        val result = manageTasksUseCase.deleteTaskByName("Unknown Task")

        assertThrows<TaskNotFoundException> { result.getOrThrow() }
    }

    @Test
    fun `deleteTaskByName() should return failure result when deletion fails`() {
        val task = buildTask(name = "Task X")
        every { taskRepository.getAllTasks() } returns Result.success(listOf(task))
        every { taskRepository.deleteTask(task.id) } returns Result.failure(NoTasksDeleted())

        val result = manageTasksUseCase.deleteTaskByName("Task X")

        assertThrows<NoTasksDeleted> { result.getOrThrow() }
    }
}
