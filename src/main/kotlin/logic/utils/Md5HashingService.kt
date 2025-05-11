package org.example.logic.utils

import java.security.MessageDigest

class Md5HashingService : HashingService {
    override fun hash(data: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(data.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun verify(data: String, hashed: String): Boolean {
        return hash(data) == hashed
    }
}