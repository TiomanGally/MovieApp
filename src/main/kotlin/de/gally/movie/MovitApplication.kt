package de.gally.movie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MovitApplication

fun main(args: Array<String>) {
    runApplication<MovitApplication>(*args)
}
