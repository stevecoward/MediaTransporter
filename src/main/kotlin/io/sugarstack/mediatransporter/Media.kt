package io.sugarstack.mediatransporter

import com.github.junrar.Archive
import java.io.File
import java.io.FileOutputStream
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

    fun extract(file: File, path: Path) {
        val archiveFile = Archive(file)
        val archiveFileHeaders = archiveFile.fileHeaders

        for (archiveFileHeader in archiveFileHeaders) {
            val fileEntry = File(Paths.get(path.toString(), archiveFileHeader.fileNameString.trim()).toString())
            val outputStream = FileOutputStream(fileEntry)
            archiveFile.extractFile(archiveFileHeader, outputStream)
            outputStream.close()
        }
    }

    private fun prepareDestination(show: ShowData): Boolean {
        show.sharePath = Paths.get(
            String.format(
                Config.properties["tvSeasonPath"] as String,
                Config.properties["mediaShareMount"] as String,
                show.title,
                show.season
            )
        )
        var success = false

        if (!Files.isDirectory(show.sharePath)) {
            success = File(show.sharePath.toString()).mkdirs()
        }

        return success
    }

    private fun prepareDestination(movie: MovieData): Boolean {
        movie.sharePath = Paths.get(
            String.format(
                Config.properties["movieRootPath"] as String,
                Config.properties["mediaShareMount"] as String,
                movie.title
            )
        )
        var success = false

        if (!Files.isDirectory(movie.sharePath)) {
            success = movie.sharePath.toFile().mkdirs()
        }

        return success
    }
}