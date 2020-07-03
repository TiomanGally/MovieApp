package de.gally.movie

import de.gally.movie.exception.Message
import de.gally.movie.movie.MovieService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.kotlin.core.publisher.toMono

@Configuration
class GatewayController {

    @Bean
    fun roomsRouter(service: MovieService) =
        router {
            accept(MediaType.APPLICATION_JSON).nest {
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
    fun isAlive(request: ServerRequest) = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(Message("App is alive!").build().toMono(), String::class.java)
}
