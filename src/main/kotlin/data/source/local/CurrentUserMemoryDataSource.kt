package org.example.data.source.local

import data.dto.UserDto
import org.example.data.source.CurrentUserDataSource

class CurrentUserMemoryDataSource : CurrentUserDataSource {
    private var currentUser: UserDto? = null

    override fun getCurrentUser(): UserDto? = currentUser


    override fun setCurrentUser(userDto: UserDto) {
        currentUser = userDto
    }

    override fun clearCurrentUser() {
        currentUser = null
    }
}