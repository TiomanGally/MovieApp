package de.gally.movit.exception

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Mono

@ControllerAdvice
class WebExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /** Handles every Throwable that is thrown by MoviT and will return an understandable ResponseEntity */
    @ExceptionHandler(Throwable::class)
    @RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun handleMoviTExceptions(exception: Throwable): Mono<ResponseEntity<String>> {
        logger.error("Message of Exception: [${exception.message}]")
        return exception.toErrorResponse()
    }
}