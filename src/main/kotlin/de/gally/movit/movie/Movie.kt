package de.gally.movit.movie

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Movie")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Movie(

        @Indexed(unique = true)
        @JsonProperty("Title")
        var title: String = "",

        @JsonProperty("Year")
        var year: String = "",

        @JsonProperty("Rated")
        var rated: String = "",

        @JsonProperty("Released")
        var release: String = "",

        @JsonProperty("Runtime")
        var runtime: String = "",

        @JsonProperty("Genre")
        var genre: String = "",

        @JsonProperty("Director")
        var director: String = "",

        @JsonProperty("Writer")
        var writer: String = "",

        @JsonProperty("Actors")
        var actor: String = "",

        @JsonProperty("Plot")
        var description: String = "",

        @JsonProperty("Language")
        var language: String = "",

        @JsonProperty("Country")
        var country: String = "",

        @JsonProperty("Awards")
        var awards: String = "",

        @JsonProperty("Poster")
        var posterLink: String = "",

        @JsonProperty("Ratings")
        var ratings: List<Rating> = emptyList(),

        @JsonProperty("Metascore")
        var metaScoreRating: String = "",

        @JsonProperty("")
        var imdbRating: String = "",

        @JsonProperty("")
        var imdbVotes: String = "",

        @JsonProperty("")
        var imdbID: String = "",

        @JsonProperty("")
        var Type: String = "",

        var DVD: String = "",

        @JsonProperty("BoxOffice")
        var boxOffice: String = "",

        @JsonProperty("Production")
        var production: String = "",

        @JsonProperty("Website")
        var website: String = "",

        @JsonProperty("Response")
        var response: String = "",

        @JsonProperty("Error")
        val error: String = "",

        var whereDoIHaveThis: List<String> = listOf()
) {
    /** If a movie does exist it will return true */
    fun doesExist() = (error != NOT_FOUND)

    private companion object {
        const val NOT_FOUND = "Movie not found!"
    }
}

data class Rating(@JsonProperty("Source") var source: String = "", @JsonProperty("Value") var year: String = "")