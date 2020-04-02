package de.gally.movit.movie.webclient

import de.gally.movit.movie.Movie
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Service
class ImdbWebClient(
        private val imdbWebClientConfig: ImdbWebClientConfig
) : BaseWebClient() {

    /** WebClient for requesting data against IMDB*/
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

        log(TargetSystem.IMDB, HttpMethod.GET, uri)

        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Movie::class.java)
                .handleClientError(TargetSystem.IMDB) {
                    this
                }
    }

    /** Object for holding the consts for this webclient*/
    private companion object {
        const val API_KEY = "apikey"
        const val TITLE = "t"
    }
}