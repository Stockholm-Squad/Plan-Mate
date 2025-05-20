package org.example.data.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T, R> tryToExecute(
    function: suspend () -> T,
    onSuccess: (T) -> R,
    onFailure: (exception: Exception) -> R,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): R = withContext(dispatcher) {
    try {
        val result = function()
        onSuccess(result)
    } catch (exception: Exception) {
        onFailure(exception)
    }
}