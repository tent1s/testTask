package com.example.testapplt.domain.model

sealed class Either<out Error, out Data> {
    class Failure<out Error>(val error: Error) : Either<Error, Nothing>()

    class Success<out Data>(val data: Data) : Either<Nothing, Data>()

    val isSuccess get() = this is Success<Data>
    val isFailure get() = this is Failure<Error>

    fun <Error> failure(error: Error) = Failure(error)
    fun <Data> success(data: Data) = Success(data)

    fun process(fnError: (Error) -> Any, fnSuccess: (Data) -> Any): Any = when (this) {
        is Failure -> fnError(error)
        is Success -> fnSuccess(data)
    }
}

fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C = {
    f(this(it))
}

fun <T, Error, Data> Either<Error, Data>.flatMap(fn: (Data) -> Either<Error, T>): Either<Error, T> =
    when (this) {
        is Either.Failure -> Either.Failure(
            error
        )
        is Either.Success -> fn(data)
    }

fun <T, Error, Data> Either<Error, Data>.map(fn: (Data) -> (T)): Either<Error, T> = this.flatMap(fn.c(::success))

fun <R, Error, Data> Either<Error, Data>.flatMapError(fn: (Error) -> Either<R, Data>): Either<R, Data> =
    when (this) {
        is Either.Success -> Either.Success(
            data
        )
        is Either.Failure -> fn(error)
    }

fun <R, Error, Data> Either<Error, Data>.mapError(fn: (Error) -> (R)): Either<R, Data> = this.flatMapError(fn.c(::failure))