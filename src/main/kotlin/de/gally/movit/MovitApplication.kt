package de.gally.movit

import de.gally.movit.exception.Message
import de.gally.movit.movie.MovieService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.kotlin.core.publisher.toMono

@SpringBootApplication
class MovitApplication

fun main(args: Array<String>) {
    runApplication<MovitApplication>(*args)
}


@Configuration
class RouterService {

    @Bean
    fun roomsRouter(service: MovieService) =
            router {
                accept(APPLICATION_JSON).nest {
                    "/api".nest {
                        "/movies".nest {
                            GET("/", service::getAllMovies)
                            GET("/{title}", service::getMovieByTitle)
                            GET("/{title}/imdb", service::requestFromImdb)
                            POST("/", service::saveMovie)
                        }
                    }
                    "/alive".nest {
                        GET("/", ::isAlive)
                    }
                }
            }

    @Suppress("UNUSED_PARAMETER")
    fun isAlive(request: ServerRequest) = ServerResponse.ok().contentType(APPLICATION_JSON).body(Message("App is alive!").build().toMono(), String::class.java)
}