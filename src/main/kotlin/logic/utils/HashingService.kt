package org.example.logic.utils

interface HashingService {
    fun hash(data: String): String
    fun verify(data: String, hashed: String): Boolean
}