package org.example.ui.features.state.model

enum class EntityStateMenuChoice(
    val choiceNumber: Int,
    val choiceMessage: String
) {
    SHOW_ALL(1, "Show all states"),
    ADD_STATE(2, "Add state"),
    EDIT_STATE(3, "Edit state"),
    DELETE_STATE(4, "Delete state"),
    BACK(5, "Back")
}