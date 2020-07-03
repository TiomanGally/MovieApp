package de.gally.movie.movie

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MovieRepository : ReactiveMongoRepository<Movie, String> {

    /** Returns a [Movie] by its [title] */
    fun getMovieByTitle(title: String): Mono<Movie>
}
