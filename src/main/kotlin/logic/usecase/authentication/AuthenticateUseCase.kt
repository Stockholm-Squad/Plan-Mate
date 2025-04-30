package org.example.logic.usecase.authentication

import com.sun.net.httpserver.Authenticator.Success
import logic.model.entities.User
import org.example.logic.repository.UserRepository

class AuthenticateUseCase(
    private val userRepository: UserRepository
) {
    // check userName done
    // check password done
    // User -> enters username and password done
    // userName -> notEmpty, doesn't start with numbers, username less than 4 characters , not more than 20 characters done
    //Password -> not empty , not less than 8 characters done
    // if username exists?
    // not exist -> print not exist message
    // exist -> check if password is correct or not
    // correct -> welcome message
    // not correct  -> incorrect credentials please try again
    fun authUser(userName:String, password:String) : Result<User>{
        return Result.failure(Throwable())
    }

}