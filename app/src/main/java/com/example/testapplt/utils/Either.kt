package com.example.testapplt.utils

sealed class Either<out Error, out Data> {
    class Failure<out Error>(val error: Error) : Either<Error, Nothing>()

    class Success<out Data>(val data: Data) : Either<Nothing, Data>()

    val isSuccess get() = this is Success<Data>
    val isFailure get() = this is Failure<Error>

    fun <Error> failure(error: Error) = Failure(error)
    fun <Data> success(data: Data) = Success(data)

}