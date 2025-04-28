package org.example.data.datasources

interface PlanMateDataSource<T> {
    fun read(): Result<List<T>>
    fun write(data: List<T>): Result<Boolean>
}