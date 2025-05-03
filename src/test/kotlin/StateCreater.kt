import logic.model.entities.ProjectState

fun createState(
    id: String,
    name: String,
):ProjectState{
return ProjectState(id=id,name=name)
}