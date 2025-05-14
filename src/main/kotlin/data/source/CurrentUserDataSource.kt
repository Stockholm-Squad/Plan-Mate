package org.example.data.source

import data.dto.UserDto

interface CurrentUserDataSource {
    fun getCurrentUser(): UserDto?
    fun setCurrentUser(userDto: UserDto)
    fun clearCurrentUser()
}