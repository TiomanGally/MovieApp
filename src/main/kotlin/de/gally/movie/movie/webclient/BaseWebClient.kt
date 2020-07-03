package de.gally.movie.movie.webclient

import de.gally.movie.exception.ExceptionMessage
import de.gally.movie.exception.InternalErrorException
import de.gally.movie.exception.ServiceIsUnavailableException
import de.gally.movie.exception.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.ConnectException
import java.net.UnknownHostException

abstract class BaseWebClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /** If the WebClient returned a not handled status this will log it */
    private fun WebClientResponseException.logNotHandledError(targetSystem: TargetSystem): WebClientResponseException {
        return this.also { logger.warn("Error with [${this.statusCode}] and body [${this.responseBodyAsString}] was returned from [$targetSystem]") }
    }

    /** Logs the Request for a WebClient */
    fun log(targetSystem: TargetSystem, method: HttpMethod, uri: String, body: String? = null) {
        val bodyMessage = if (body != null) "and body [$body]" else ""
        logger.info("Making [$method] request against [$targetSystem] with uri [$uri] $bodyMessage")
    }

    /**
     * This functions handles all errors. With this function it is possible to easily handle a BAD REQUEST or a NOT FOUND.
     * It is possible to deal with the [WebClientResponseException] which is thrown by the client. And this contains the error
     * and the statusCode.
     */
    fun <T> Mono<T>.handleClientError(
        targetSystem: TargetSystem,
        specificCalls: WebClientResponseException.() -> Throwable = {
            this.logNotHandledError(targetSystem)
        }
    ): Mono<T> {
        return this.onErrorMap {
            when (it) {
                is WebClientResponseException -> {
                    when (it.statusCode) {
                        HttpStatus.UNAUTHORIZED -> UnauthorizedException(targetSystem.unauthorizedMessage)
                        HttpStatus.SERVICE_UNAVAILABLE -> ServiceIsUnavailableException(targetSystem.notAvailableMessage)
                        HttpStatus.INTERNAL_SERVER_ERROR -> InternalErrorException(targetSystem.internalErrorMessage)
                        else -> specificCalls(it)
                    }
                }
                is UnknownHostException -> ServiceIsUnavailableException(targetSystem.unknownHostMessage)
                is ConnectException -> ServiceIsUnavailableException(targetSystem.notAvailableMessage)
                else -> it
            }
        }
    }

    /** This holds the special [ExceptionMessage] for a specific target system if something bad happens */
    enum class TargetSystem(
        val notAvailableMessage: ExceptionMessage,
        val unauthorizedMessage: ExceptionMessage,
        val internalErrorMessage: ExceptionMessage,
        val unknownHostMessage: ExceptionMessage
    ) {
        IMDB(
            ExceptionMessage.IMDB_NOT_AVAILABLE,
            ExceptionMessage.IMDB_UNAUTHORIZED,
            ExceptionMessage.IMDB_INTERNAL_SERVER_ERROR,
            ExceptionMessage.IMDB_UNKNOWN_HOST
        )
    }
}
