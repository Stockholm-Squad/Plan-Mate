package org.example.data.source.local.csv_reader_writer

interface IReaderWriter<T> {
    suspend fun read(): List<T>
    suspend fun overWrite(data: List<T>): Boolean
    suspend fun append(data: List<T>): Boolean
}