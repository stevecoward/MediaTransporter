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

/**
 * Processes either a ShowData or MovieData object.
 */
open class Media {
    /**
     * @constructor accepts a ShowData [mediaData] object and ensures that the filesystem is ready for the new media.
     */
    constructor(mediaData: ShowData) {
        prepareDestination(mediaData)
        KotlinLogging
    }

    /**
     * @constructor accepts a MovieData [mediaData] object that ensures that the filesystem is ready for the new media.
     */
    constructor(mediaData: MovieData) {
        prepareDestination(mediaData)
    }

    /**
     * Checks that directories to hold a [show] exist and if not
     * then create the directories and returns true or false if
     * the task was a success.
     */
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

    /**
     * Checks that directories to hold a [movie] exist and if not
     * then create the directories and returns true or false if
     * the task was a success.
     */
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

    /**
     * Extracts a media [file] in [path] from a rar archive.
     */
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

    /**
     * Looks for files in [path] matching [regexString] and returns true if files were found
     * otherwise false. This is used in process() to determine if a file is a rar archive or a media file.
     */
    private fun filesExist(path: Path, regexString: String?): Boolean =
        Utils.findMediaFiles(path, false, regexString).count() > 0

    /**
     * Returns true or false if the [filePath] contains "sample" in its title. We don't process samples.
     */
    private fun isSampleFile(filePath: Path): Boolean = filePath.toString().contains("sample", ignoreCase = true)

    /**
     * Returns true or false if the [filePath] is a rar archive
     */
    private fun isRarFile(filePath: Path): Boolean = filePath.toString().endsWith("rar", ignoreCase = true)

    /**
     * Compares the file name of the release in [downloadsPath] against files in [sharePath] that match [regexString].
     * If the file isn't a sample file and the file exists in the media share then skip extract/copy tasks. If the file
     * doesn't exist then determine if if the file can be directly copied or or must be extracted from the rar archive.
     *
     * TODO: If a rar and mkv exist in the source path, the message 'Found' is logged twice.
     * TODO: We need to handle release files that have subs rar archives.
     */
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