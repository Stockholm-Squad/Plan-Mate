package org.example.utils

import java.security.MessageDigest

fun hashToMd5(password: String): String {
    val bytes = MessageDigest.getInstance("MD5").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}