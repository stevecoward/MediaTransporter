package io.sugarstack.mediatransporter.media

import com.github.junrar.Archive
import io.sugarstack.mediatransporter.Config
import io.sugarstack.mediatransporter.TransportLogger
import io.sugarstack.mediatransporter.Utils
import io.sugarstack.mediatransporter.media.data.MovieData
import io.sugarstack.mediatransporter.media.data.ShowData
import mu.KotlinLogging
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

open class Media {
    constructor(mediaData: ShowData) {
        prepareDestination(mediaData)
        KotlinLogging
    }

    constructor(mediaData: MovieData) {
        prepareDestination(mediaData)
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

    private fun extract(file: File, path: Path) {
        val archiveFile = Archive(file)
        val archiveFileHeaders = archiveFile.fileHeaders

        for (archiveFileHeader in archiveFileHeaders) {
            val fileEntry = File(Paths.get(path.toString(), archiveFileHeader.fileNameString.trim()).toString())
            val outputStream = FileOutputStream(fileEntry)
            archiveFile.extractFile(archiveFileHeader, outputStream as OutputStream?)
            outputStream.close()
        }
    }

    private fun filesExist(path: Path, regexString: String?): Boolean =
        Utils.findMediaFiles(path, false, regexString).count() > 0

    private fun isSampleFile(filePath: Path): Boolean = filePath.toString().contains("sample", ignoreCase = true)
    private fun isRarFile(filePath: Path): Boolean = filePath.toString().endsWith("rar", ignoreCase = true)

//    TODO: Handle subs rar files properly

    fun process(downloadsPath: Path, sharePath: Path, regexString: String?, mediaTypeLogger: TransportLogger) {
        when (!isSampleFile(downloadsPath)) {
            true -> when (filesExist(sharePath, regexString)) {
                true -> mediaTypeLogger.info("Found $this") // TODO: If a rar and mkv exist in source, show is "found" twice
                false -> {
                    mediaTypeLogger.info("Copying $this to destination")
                    val mediaFile = downloadsPath.toFile()
                    when (!isRarFile(downloadsPath)) {
                        true -> {
                            mediaFile.copyTo(sharePath.resolve(downloadsPath.fileName).toFile())
                            return
                        }
                        else -> extract(mediaFile, sharePath)
                    }
                }
            }
        }
    }
}