package io.sugarstack.mediatransporter.media

import io.sugarstack.mediatransporter.TransportLogger
import io.sugarstack.mediatransporter.media.data.MovieData

private val logger = TransportLogger()

class Movie(private val data: MovieData) : Media(data) {
    private var moviePath = data.sharePath

    override fun toString(): String {
        return "${data.title} (${data.year})"
    }

    fun process() = super.process(data.path, moviePath, null, logger)
}