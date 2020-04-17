package de.gally.movit

import de.gally.movit.exception.Message
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
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
    fun roomsRouter() = router {
        accept(APPLICATION_JSON)
                .nest {
                    GET("/alive") {
                        ok().contentType(APPLICATION_JSON).body(Message("App is alive!").build().toMono(), String::class.java)
                    }
                }
    }
}