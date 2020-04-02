package de.gally.movit.movie.webclient

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.net.URL

@Primary
@Configuration
@ConfigurationProperties(prefix = "imdb")
class ImdbWebClientConfig {
    lateinit var baseUrl: URL
    lateinit var apiKey: String
}