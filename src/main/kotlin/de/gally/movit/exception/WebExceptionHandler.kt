package de.gally.movit.exception

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@ControllerAdvice
class WebExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /** Handles every Throwable that is thrown by MoviT and will return an understandable ResponseEntity */
    @ExceptionHandler(Throwable::class)
    @RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun handleMoviTExceptions(exception: Throwable) = mapToErrorResponse(exception)

    /**
     * Handles every ServerWebInputException which is thrown f. e. by a wrong input.
     * This will parse the message into beautiful and understandable ResponseEntity
     */
    @ExceptionHandler(ServerWebInputException::class)
    @RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun handleWrongInputs(exception: ServerWebInputException) = mapToErrorResponse(exception)

    /** If a required requestBody is missing it will print a readable message without giving and private paths to the user */
    @ExceptionHandler(IllegalStateException::class)
    @RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun handleMissingRequestBody(exception: IllegalStateException) = mapToErrorResponse(exception)

    /** Maps the exception to a [ResponseEntity] with a message and headers */
    private fun mapToErrorResponse(exception: Throwable): Mono<ResponseEntity<String>> {
        logger.error("Message of Exception: [${exception.message}]")
        return exception.toErrorResponse()
    }
}