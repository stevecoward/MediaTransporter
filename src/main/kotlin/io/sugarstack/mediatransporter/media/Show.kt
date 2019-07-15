package io.sugarstack.mediatransporter.media

import io.sugarstack.mediatransporter.media.data.ShowData
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Show(private val data: ShowData) : Media(data) {
    private var showSeasonPath = data.sharePath

    override fun toString(): String {
        return "${data.title}: Season ${data.season} Episode ${data.episode}"
    }

    fun process() = super.process(data.path, showSeasonPath, ".+[sS]${data.season}[eE]${data.episode}.+", logger)
}