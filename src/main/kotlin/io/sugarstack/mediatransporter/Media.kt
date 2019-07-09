package io.sugarstack.mediatransporter

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

open class Media {
    constructor(mediaData: ShowData) {
        prepareDestination(mediaData)
    }

    constructor(mediaData: MovieData) {
        prepareDestination(mediaData)
    }

    fun process(): Boolean {

        return true
    }

    private fun prepareDestination(show: ShowData): Boolean {
        val rootTvPath = Paths.get(String.format(Config.tvRootPath, Config.mediaShareMount, show.title))
        val seasonPath = Paths.get(String.format(Config.tvSeasonPath, Config.mediaShareMount, show.title, show.season))

        if (!Files.isDirectory(seasonPath)) {
            val success = File(seasonPath.toString()).mkdirs()
        }

        return true
    }

    private fun prepareDestination(movie: MovieData): Boolean {

        return true
    }
}