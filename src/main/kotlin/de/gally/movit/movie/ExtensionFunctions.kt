package de.gally.movit.movie

private inline fun <T, R> T?.ifNull(body: () -> R): R where T : R = this ?: body()