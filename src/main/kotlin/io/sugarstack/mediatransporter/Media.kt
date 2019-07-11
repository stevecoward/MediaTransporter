package io.sugarstack.mediatransporter

import java.io.File
import java.nio.file.Files
import java.nio.file.Files.isRegularFile
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


open class Media {
    constructor(mediaData: ShowData) {
        prepareDestination(mediaData)
    }

    constructor(mediaData: MovieData) {
        prepareDestination(mediaData)
    }

    fun getExistingMedia(mediaShare: Path, data: ShowData): Optional<Path>? {

        return Files.walk(data.path).use { path ->
            path
                .filter { isRegularFile(it) }
                .findAny()
            //                .collect(Collectors.toList())
        }
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