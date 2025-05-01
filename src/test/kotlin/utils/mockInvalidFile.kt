package utils

import java.io.File

// Helper function to create a mock file that throws an exception
fun mockInvalidFile(): File {
    return object : File("") {
        override fun exists(): Boolean = true
        fun readLines(): List<String> {
            throw Throwable("Simulated file read error")
        }
        fun appendText(text: String) {
            throw Throwable("Simulated file write error")
        }
        fun writeText(text: String) {
            throw Throwable("Simulated file write error")
        }
    }
}
