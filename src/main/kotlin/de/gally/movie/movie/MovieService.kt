package de.gally.movie.movie

import de.gally.movie.exception.Message
import de.gally.movie.movie.webclient.ImdbWebClient
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
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
            .flatMap { ServerResponse.ok().contentType(APPLICATION_JSON).body(it.toMono(), Movie::class.java) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /** Request a [Movie] from IMDB and returns it */
    fun requestFromImdb(request: ServerRequest): Mono<ServerResponse> {
        return imdbWebClient
            .requestMovieByTitle(request.pathVariable("title"))
            .flatMap { ServerResponse.ok().contentType(APPLICATION_JSON).body(it.toMono(), Movie::class.java) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    /** Saves a new [Movie] in database or finds it if it does exist already */
    fun saveMovie(request: ServerRequest): Mono<ServerResponse> {
        return request
            .bodyToMono(Movie::class.java)
            .flatMap { movie ->
                movieRepository
                    .getMovieByTitle(movie.title)
                    .doOnNext { logger.info("Successfully found movie with title [${it.title}] in database") }
                    .switchIfEmpty {
                        if (movie.title.isNotBlank())
                            movieRepository
                                .save(movie)
                                .doOnNext { logger.info("Successfully persisted movie with title [${it.title}] in database") }
                        else Mono.empty()
                    }
                    .flatMap { ServerResponse.ok().contentType(APPLICATION_JSON).body(it.toMono(), Movie::class.java) }
                    .switchIfEmpty(
                        ServerResponse.badRequest()
                            .body(Message("Movie has empty title").build().toMono(), String::class.java)
                    )
            }
    }
}
