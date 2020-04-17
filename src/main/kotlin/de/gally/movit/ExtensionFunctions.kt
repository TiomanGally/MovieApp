package de.gally.movit

inline fun <T, R> T?.ifNull(body: () -> R): R where T : R = this ?: body()