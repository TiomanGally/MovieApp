package de.gally.movit.exception

import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

sealed class MoviTException(message: String) : Throwable(message)
open class InvalidException(message: ExceptionMessage) : MoviTException(message.message)
class UnauthorizedException(message: ExceptionMessage) : MoviTException(message.message)
class ServiceIsUnavailableException(message: ExceptionMessage) : MoviTException(message.message)
class InternalErrorException(message: ExceptionMessage) : MoviTException(message.message)

/** If an error is thrown this will parse the error to a [ResponseEntity] */
fun Throwable.toErrorResponse(): Mono<ResponseEntity<String>> {
    fun createMonoResponseEntity(httpStatus: HttpStatus, message: String): Mono<ResponseEntity<String>> {
        return ResponseEntity
                .status(httpStatus)
                .body(Message(message).build())
                .toMono()
    }

    if (this is MoviTException) {
        return when (this) {
            is UnauthorizedException -> createMonoResponseEntity(HttpStatus.NOT_FOUND, this.localizedMessage)
            is ServiceIsUnavailableException -> createMonoResponseEntity(HttpStatus.BAD_REQUEST, this.localizedMessage)
            is InternalErrorException -> createMonoResponseEntity(HttpStatus.I_AM_A_TEAPOT, this.localizedMessage)
            is InvalidException -> createMonoResponseEntity(HttpStatus.BAD_REQUEST, this.localizedMessage)
        }
    }
    return createMonoResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionMessage.INTERNAL_SERVER_ERROR.message)
}

/** Holds all exceptionMessages which can be thrown in MoviT */
enum class ExceptionMessage(val message: String) {
    IMDB_NOT_AVAILABLE("IMDB is currently not available"),
    IMDB_UNAUTHORIZED("User for IMDB is unauthorized"),
    IMDB_UNKNOWN_HOST("Could not resolve IMDB URI"),
    IMDB_INTERNAL_SERVER_ERROR("IMDB fucked something up"),
    INTERNAL_SERVER_ERROR("Something went really wrong :(")
}

/** Creates a JSON exception message info box */
data class Message(val message: String) {
    fun build() = JSONObject()
            .put("message", message)
            .put("timestamp", LocalDateTime.now())
            .toString()
}