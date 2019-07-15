package io.sugarstack.mediatransporter.media

import io.sugarstack.mediatransporter.media.data.MovieData
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Movie(private val data: MovieData) : Media(data) {
    private var moviePath = data.sharePath

    override fun toString(): String {
        return "${data.title} (${data.year})"
    }

    fun process() = super.process(data.path, moviePath, null, logger)
}