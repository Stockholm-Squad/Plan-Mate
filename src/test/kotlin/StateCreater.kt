import org.example.logic.entities.EntityState
import org.example.logic.utils.toSafeUUID

fun createState(
    id: String,
    name: String,
): EntityState {
    return EntityState(id = id.toSafeUUID(), name = name)
}