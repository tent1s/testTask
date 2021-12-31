package com.example.testapplt.domain.model

import android.annotation.SuppressLint
import okhttp3.ResponseBody
import retrofit2.*
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultCallAdapterFactory() : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<out Any, Call<Either<ErrorReason, *>>>? {
        if (returnType !is ParameterizedType) {
            return null
        }

        val containerType = getParameterUpperBound(0, returnType)

        if (getRawType(containerType) != Either::class.java)
            return null

        if (containerType !is ParameterizedType) {
            return null
        }

        val errorType = getParameterUpperBound(0, containerType)
        if (getRawType(errorType) != ErrorReason::class.java)
            return null


        val errorBodyConverter = retrofit.responseBodyConverter<ErrorReason.ServerError>(
            ErrorReason.ServerError::class.java,
            annotations
        )

        val resultType = getParameterUpperBound(1, containerType)

        return ResultCallAdapter(
            resultType,
            errorBodyConverter
        )
    }

    private class ResultCallAdapter<R, T>(
        private val resultType: Type,
        private val errorBodyConverter: Converter<ResponseBody, ErrorReason.ServerError>
    ) :
        CallAdapter<R, Call<Either<ErrorReason, T>>> {

        override fun adapt(call: Call<R>): Call<Either<ErrorReason, T>> =
            ResultCallWrapper(
                call,
                errorBodyConverter
            )

        override fun responseType() = resultType
    }

    private class ResultCallWrapper<F, T>(
        private val delegate: Call<T>,
        private val errorBodyConverter: Converter<ResponseBody, ErrorReason.ServerError>
    ) : Call<Either<ErrorReason, F>> {

        override fun execute() = wrapResponse(delegate.execute())

        @SuppressLint("Incorrect order")
        override fun enqueue(callback: Callback<Either<ErrorReason, F>>) {
            try {
                delegate.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        var wrapResponse = wrapResponse(response)

                        callback.onResponse(this@ResultCallWrapper, wrapResponse)
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        Timber.d(t, "NetworkError")
                        val failedResponse =
                            Response.success(Either.Failure(ErrorReason.NetworkError(t)) as Either<ErrorReason, F>)
                        callback.onResponse(this@ResultCallWrapper, failedResponse)
                    }
                })
            } catch (e: Exception) {

                Timber.d(e, "UnexpectedError on network call")
                val unexpectedErrorResponse =
                    Response.success(Either.Failure(ErrorReason.UnexpectedError(e)) as Either<ErrorReason, F>)
                callback.onResponse(this@ResultCallWrapper, unexpectedErrorResponse)
            }
        }

        override fun isExecuted() = delegate.isExecuted

        override fun isCanceled() = delegate.isCanceled

        override fun cancel() = delegate.cancel()

        override fun request() = delegate.request()

        override fun clone() =
            ResultCallWrapper<F, T>(
                delegate.clone(),
                errorBodyConverter
            )


        @Suppress("UNCHECKED_CAST")
        private fun wrapResponse(response: Response<T>): Response<Either<ErrorReason, F>> {
            return try {
                if (response.isSuccessful) {
                    Response.success(Either.Success(response.body()) as Either<ErrorReason, F>)
                } else {
                    Response.success(
                        Either.Failure(
                            errorBodyConverter.convert(response.errorBody() as ResponseBody)
                        ) as Either<ErrorReason, F>
                    )
                }
            } catch (e: Exception) {
                Response.success(Either.Failure(ErrorReason.UnexpectedError(e)) as Either<ErrorReason, F>)
            }
        }

    }
}