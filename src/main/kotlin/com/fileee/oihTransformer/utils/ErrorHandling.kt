package com.fileee.oihTransformer.utils

sealed class HandlerException: Exception() {
    data class ParameterNull(val name: String): HandlerException()

    /**
     * When converting jsonObjects two errors can occur, required values may not have a value or the value might be of the
     *  wrong expected
     */
    sealed class ParseException: HandlerException() {
        data class MissingRequiredValue(val name: String): ParseException() {
            override val message: String?
                get() = "Property $name was not found, but is required"
        }
        data class InvalidTypeException(val expected: String, val actual: String): ParseException() {
            override val message: String?
                get() = "Expected expected $expected but got $actual"
        }
    }

    class UnhandledException(val err: Throwable): HandlerException()
}
typealias ParameterNull = HandlerException.ParameterNull
typealias UnhandledException = HandlerException.UnhandledException

typealias ParseError = HandlerException.ParseException
typealias InvalidTypeError = HandlerException.ParseException.InvalidTypeException
typealias MissingRequiredValue = HandlerException.ParseException.MissingRequiredValue