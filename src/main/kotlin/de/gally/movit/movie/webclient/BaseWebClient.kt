package de.gally.movit.movie.webclient

import de.gally.movit.movie.exception.ExceptionMessage
import de.gally.movit.movie.exception.InternalErrorException
import de.gally.movit.movie.exception.ServiceIsUnavailableException
import de.gally.movit.movie.exception.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.ConnectException
import java.net.UnknownHostException

abstract class BaseWebClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Sets all required headers for MBC and also the user for Authorization
     *
     * @param user for the target system
     */
    fun WebClient.Builder.setHeaderWithUser(user: WebClientUser): WebClient.Builder {
        return this.defaultHeaders {
            it.apply {
                this.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                this.setBasicAuth(user.username, user.password)
            }
        }
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
    fun <T> Mono<T>.handleClientError(targetSystem: TargetSystem, specificCalls: WebClientResponseException.() -> Throwable): Mono<T> {
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

    /** Param Values have to contain an ':' in front of the param because it will be replaced with a value */
    companion object ParamValues {
    }

    /** Replaces a param (Regex: ':paramName') with its value. */
    fun String.setParam(param: String, value: String) = this.replace(param, value)

    /** This holds the special [ExceptionMessage] for a specific target system if something bad happens */
    enum class TargetSystem(
            val notAvailableMessage: ExceptionMessage,
            val unauthorizedMessage: ExceptionMessage,
            val internalErrorMessage: ExceptionMessage,
            val unknownHostMessage: ExceptionMessage) {
        IMDB(ExceptionMessage.IMDB_NOT_AVAILABLE, ExceptionMessage.IMDB_UNAUTHORIZED, ExceptionMessage.IMDB_INTERNAL_SERVER_ERROR, ExceptionMessage.IMDB_UNKNOWN_HOST)
    }

    /** Holds username and password for a webClient */
    data class WebClientUser(val username: String, val password: String)
}