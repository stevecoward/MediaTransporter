package io.sugarstack.mediatransporter.media

import io.sugarstack.mediatransporter.TransportLogger
import io.sugarstack.mediatransporter.media.data.ShowData

private val logger = TransportLogger()

/**
 * Stores data about a show and exposes a method to process data.
 *
 * @property data the ShowData object holding show details.
 */
class Show(private val data: ShowData) : Media(data) {
    private var showSeasonPath = data.sharePath

    /**
     * Returns a human-friendly value of the show title, season and episode.
     */
    override fun toString(): String {
        return "${data.title}: Season ${data.season} Episode ${data.episode}"
    }

    /**
     * Executes process() in the super Media class for a show.
     */
    fun process() = super.process(data.path, showSeasonPath, ".+[sS]${data.season}[eE]${data.episode}.+", logger)
}