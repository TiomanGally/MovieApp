package de.gally.movit.movie

import de.gally.movit.exception.ExceptionMessage
import de.gally.movit.exception.MovieInvalidException
import de.gally.movit.movie.webclient.ImdbWebClient
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("v1/movies")
class MovieController(
        private val imdbWebClient: ImdbWebClient,
        private val movieRepository: MovieRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /** Returns all [Movie]s from database */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllMovies(): Mono<ResponseEntity<MutableList<Movie>>> {
        return movieRepository
                .findAll()
                .collectList()
                .map { ResponseEntity.ok(it) }
    }

    /** Returns a [Movie] found by its [title] */
    @GetMapping("/{title}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMovieByTitle(@PathVariable("title") title: String): Mono<ResponseEntity<Movie>> {
        return movieRepository
                .getMovieByTitle(title)
                .map { ResponseEntity.ok(it) }
    }

    /** Request a [Movie] from IMDB and returns it */
    @GetMapping("/{title}/request", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun requestNewMovie(@PathVariable("title") title: String): Mono<ResponseEntity<Movie>> {
        return imdbWebClient
                .requestMovieByTitle(title)
                .doOnNext { logger.info("Successfully requested IMDB for received Movie with title [$title]") }
                .map { if (it.doesExist()) ResponseEntity.ok(it) else ResponseEntity.notFound().build() }
    }

    /** Saves a new [Movie] in database */
    @PostMapping
    fun saveMovie(@RequestBody movie: Movie): Mono<ResponseEntity<Movie>> {
        if (movie.title.isBlank()) {
            throw MovieInvalidException(ExceptionMessage.INVALID_MOVIE_PAYLOAD)
        }
        return movieRepository
                .save(movie)
                .map { ResponseEntity.ok(it) }
    }
}