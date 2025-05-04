import logic.model.entities.ProjectState
import org.example.logic.usecase.extention.toSafeUUID

fun createState(
    id: String,
    name: String,
): ProjectState {
    return ProjectState(id = id.toSafeUUID(), name = name)
}