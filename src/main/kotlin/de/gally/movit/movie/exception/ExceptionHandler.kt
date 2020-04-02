package de.gally.movit.movie.exception

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
class MovieInvalidException(message: ExceptionMessage) : InvalidException(message)

/** If an error is thrown this will parse the error to a [ResponseEntity] */
fun Throwable.toErrorResponse(): Mono<ResponseEntity<String>> {
    fun createMonoResponseEntity(httpStatus: HttpStatus, message: String): Mono<ResponseEntity<String>> {
        return ResponseEntity
                .status(httpStatus)
                .body(JSONObject()
                        .put("message", message)
                        .put("timestamp", LocalDateTime.now())
                        .toString())
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

enum class ExceptionMessage(val message: String) {
    INVALID_MOVIE_PAYLOAD("Received Movie is invalid"),
    IMDB_NOT_AVAILABLE("IMDB is currently not available"),
    IMDB_UNAUTHORIZED("User for IMDB is unauthorized"),
    IMDB_UNKNOWN_HOST("Could not resolve IMDB URI"),
    IMDB_INTERNAL_SERVER_ERROR("IMDB fucked something up"),
    INTERNAL_SERVER_ERROR("Something went really wrong :(")
}
