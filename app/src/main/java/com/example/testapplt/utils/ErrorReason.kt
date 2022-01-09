package com.example.testapplt.utils

import com.squareup.moshi.JsonClass

sealed class ErrorReason : ReadableError {
    abstract override val message: String

    @JsonClass(generateAdapter = true)
    data class ServerError(val errors: Map<String, List<Error>>) : ErrorReason() {
        override val message =
            when (errors.size) {
                0 -> errors.entries.first().value.first().message
                1 -> {
                    val revoErrors = errors.entries.iterator().next().value
                    if (revoErrors.size == 1) {
                        revoErrors.first().message
                    } else {
                        "Multiple data error"
                    }
                }
                else -> "Multiple data error"
            }
    }

    data class NetworkError(val throwable: Throwable) : ErrorReason() {
        override val message: String = throwable.localizedMessage
    }

    data class UnexpectedError(val exception: Exception) : ErrorReason() {
        override val message: String = exception.localizedMessage
    }
}

interface ReadableError {
    val message: String
}

@JsonClass(generateAdapter = true)
data class Error(val error: String, override val message: String) : ReadableError