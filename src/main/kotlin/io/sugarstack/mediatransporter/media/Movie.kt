package io.sugarstack.mediatransporter.media

import io.sugarstack.mediatransporter.TransportLogger
import io.sugarstack.mediatransporter.media.data.MovieData

private val logger = TransportLogger()

/**
 * Stores data about a movie and exposes a method to process data.
 *
 * @property data the MovieData object holding movie details.
 */
class Movie(private val data: MovieData) : Media(data) {
    private var moviePath = data.sharePath

    /**
     * Returns a human-friendly value of the movie title and release year.
     */
    override fun toString(): String {
        return "${data.title} (${data.year})"
    }

    /**
     * Executes process() in the super Media class for a movie.
     */
    fun process() = super.process(data.path, moviePath, null, logger)
}