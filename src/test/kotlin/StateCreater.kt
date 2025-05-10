import org.example.logic.entities.ProjectState
import org.example.logic.utils.toSafeUUID

fun createState(
    id: String,
    name: String,
): ProjectState {
    return ProjectState(id = id.toSafeUUID(), name = name)
}