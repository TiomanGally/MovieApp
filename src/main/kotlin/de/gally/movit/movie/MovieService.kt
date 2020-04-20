package de.gally.movit.movie

import de.gally.movit.movie.webclient.ImdbWebClient
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class MovieService(
        private val imdbWebClient: ImdbWebClient,
        private val movieRepository: MovieRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /** Returns all [Movie]s from database */
    fun getAllMovies(request: ServerRequest) = ServerResponse.ok()
            .contentType(APPLICATION_JSON)
            .body(movieRepository.findAll(), Movie::class.java)
            .doOnNext { logger.info("Successfully got all Movies from database") }

    /** Returns a [Movie] found by its title */
    fun getMovieByTitle(request: ServerRequest): Mono<ServerResponse> {
        return movieRepository
                .getMovieByTitle(request.pathVariable("title"))
                .doOnNext { logger.info("Successfully found movie with title [${it.title}] from database") }
                .createResponse()
    }

    /** Request a [Movie] from IMDB and returns it */
    fun requestFromImdb(request: ServerRequest): Mono<ServerResponse> {
        return imdbWebClient
                .requestMovieByTitle(request.pathVariable("title"))
                .createResponse()
    }

    /** Saves a new [Movie] in database */
    fun saveMovie(request: ServerRequest): Mono<ServerResponse> {
        return request
                .bodyToMono(Movie::class.java)
                .flatMap {
                    if (it.title.isNotBlank())
                        movieRepository.save(it)
                    else Mono.empty()
                }.doOnNext { logger.info("Successfully saved movie with title [${it.title}] in database") }
                .createResponse()
    }

    /** Returns a [ServerResponse] if the movie is present. Otherwise a 404 is returned */
    private fun Mono<Movie>.createResponse() = this
            .flatMap { ServerResponse.ok().contentType(APPLICATION_JSON).body(it.toMono(), Movie::class.java) }
            .switchIfEmpty(ServerResponse.notFound().build())
}