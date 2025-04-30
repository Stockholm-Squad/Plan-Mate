package org.example.data.datasources

interface PlanMateDataSource<T> {
    fun read(): Result<List<T>>
    fun overWrite(model: List<T>): Result<Boolean>
    fun append(model: List<T>): Result<Boolean>
}