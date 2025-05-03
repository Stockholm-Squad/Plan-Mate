package org.example.logic.usecase.extention

fun String.isValidLength(length: Int): Boolean {
    return this.length <= length
}

 fun String.isLetterOrWhiteSpace(): Boolean {
    return this.all { char -> char.isLetter() || char.isWhitespace() }
}