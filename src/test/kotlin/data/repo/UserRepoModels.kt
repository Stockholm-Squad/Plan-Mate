package data.repo


import data.dto.UserDto
import org.example.logic.entities.User
import org.example.logic.entities.UserRole

import org.example.logic.utils.Md5HashingService
import java.util.UUID

val firstUserId = UUID.randomUUID()
val secondUserId = UUID.randomUUID()
val thirdUserId = UUID.randomUUID()
val hashingService = Md5HashingService()
val userDto = UserDto(
    id = firstUserId.toString(),
    username = "UserName",
    hashedPassword = hashingService.hash("usernamepassword"),
    role = UserRole.MATE.name
)
val user = User(
    id = firstUserId,
    username = "UserName",
    hashedPassword = hashingService.hash("usernamepassword"),
    userRole = UserRole.MATE
)
val usersList = listOf(
    User(
        id = firstUserId,
        username = "UserName1",
        hashedPassword = hashingService.hash("usernamepassword1"),
        userRole = UserRole.MATE
    ), User(
        id = secondUserId,
        username = "UserName2",
        hashedPassword = hashingService.hash("usernamepassword2"),
        userRole = UserRole.MATE
    ),
    User(
        id = thirdUserId,
        username = "UserName3",
        hashedPassword = hashingService.hash("usernamepassword3"),
        userRole = UserRole.MATE
    )
)
val userDtoList = listOf(
    UserDto(
        id = firstUserId.toString(),
        username = "UserName1",
        hashedPassword = hashingService.hash("usernamepassword1"),
        role = UserRole.MATE.name
    ), UserDto(
        id = secondUserId.toString(),
        username = "UserName2",
        hashedPassword = hashingService.hash("usernamepassword2"),
        role = UserRole.MATE.name
    ),
    UserDto(
        id = thirdUserId.toString(),
        username = "UserName3",
        hashedPassword = hashingService.hash("usernamepassword3"),
        role = UserRole.MATE.name
    )
)