package de.gally.movie.movie.webclient

import de.gally.movie.movie.Movie
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class ImdbWebClient(
    private val imdbWebClientConfig: ImdbWebClientConfig
) : BaseWebClient() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /** WebClient for requesting data against IMDB */
    private val webClient by lazy {
        WebClient.builder()
            .baseUrl(imdbWebClientConfig.baseUrl.toString())
            .build()
    }

    /** Requesting a [Movie] by its [title] from IMDB */
    fun requestMovieByTitle(title: String): Mono<Movie> {
        val uri = UriComponentsBuilder.newInstance()
            .queryParam(API_KEY, imdbWebClientConfig.apiKey)
            .queryParam(TITLE, title)
            .build()
            .toUriString()

        log(TargetSystem.IMDB, HttpMethod.GET, imdbWebClientConfig.baseUrl.toString() + uri)

        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Movie::class.java)
            .handleClientError(TargetSystem.IMDB)
            .doOnNext { logger.info("Successfully requested IMDB for received Movie with title [$title]") }
            .flatMap { if (it.doesExist()) it.toMono() else Mono.empty() }
    }

    /** Object for holding the constants for this webclient */
    private companion object {
        const val API_KEY = "apikey"
        const val TITLE = "t"
    }
}
