package org.example.data.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <T> executeSafelyWithContext(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    onSuccess: suspend () -> T,
    onFailure: (ex: Exception) -> T
): T = withContext(dispatcher) {
    try {
        onSuccess()
    } catch (ex: Exception) {
        onFailure(ex)
    }
}

//TODO: Check Package Name

suspend fun <T> tryToExecute(
    function: suspend () -> T,
    onSuccess: (T) -> Unit,
    onFailure: (exception: Exception) -> Unit,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) = withContext(dispatcher) {
    try {
        val result = function()
        onSuccess(result)
    } catch (exception: Exception) {
        onFailure(exception)
    }
}