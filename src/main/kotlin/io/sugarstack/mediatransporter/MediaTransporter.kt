package io.sugarstack.mediatransporter

import com.github.ajalt.clikt.core.CliktCommand
import io.sugarstack.mediatransporter.filesystem.Storage
import io.sugarstack.mediatransporter.media.Movie
import io.sugarstack.mediatransporter.media.Show
import io.sugarstack.mediatransporter.media.data.MovieData
import io.sugarstack.mediatransporter.media.data.ShowData
import java.io.File
import java.nio.file.Path
import java.util.regex.Pattern

/**
 * Contains the main entry point for the program.
 */
class MediaTransporter {
    companion object {
        /**
         * Accepts an Array of [args] from the command line and passes them to MediaTransporterCli()
         */
        @JvmStatic fun main(args: Array<String>) {
            MediaTransporterCli().main(args)
        }
    }
}

/**
 * Extends the CliktCommand class to expose cli-related functionality. Currently not in use
 * but future release will have command line options.
 */
class MediaTransporterCli : CliktCommand() {
    private val propertiesFileName: File = File("mediatransporter.properties")
    private val shows: MutableList<ShowData> = ArrayList()
    private val movies: MutableList<MovieData> = ArrayList()
    private val showSeasonEpBasePattern = Pattern.compile("^.+([Ss]\\d{1,}[Ee]\\d{1,})\\..+\$")

    /**
     * Tries to match a [file] path against a regular expression [pattern] that captures: title, year and resolution
     * from the file name. Any matches are added to MutableList<MovieData> object 'movies'.
     */
    private fun matchAndAddMovie(file: Path, pattern: Pattern) {
        val moviesMatches = pattern.matcher(file.fileName.toString())
        while (moviesMatches.find()) {
            val parsedMovieDetails = MovieData(
                moviesMatches.group("title"),
                moviesMatches.group("year"),
                moviesMatches.group("resolution"),
                file.toAbsolutePath()
            )
            movies.add(parsedMovieDetails)
        }
    }

    /**
     * Tries to match a [file] path against a regular expression [pattern] that that captures: title, season and episode
     * from the file name. Any matches are added to MutableList<ShowData> object 'shows'.
     */
    private fun matchAndAddShow(file: Path, pattern: Pattern) {
        val showsMatches = pattern.matcher(file.fileName.toString())
        while (showsMatches.find()) {
            val parsedShowDetails = ShowData(
                showsMatches.group("title"),
                showsMatches.group("season"),
                showsMatches.group("episode"),
                file.toAbsolutePath()
            )
            shows.add(parsedShowDetails)
        }
    }

    /**
     * The entry point for the cli class that overrides the CliktCommand run() method.
     */
    override fun run() {
        Config(propertiesFileName)

        val storage = Storage()
        storage.determineCapacity()

        /**
         * Finds any files in Config.properties["completedDownloadPath"] matching file
         * extensions: mkv, avi, mp4, mov, rar, and filter the results with a regular expression
         * that grabs any file name containing 'S<digit><digit>E<digit><digit>'. Doing an initial
         * filter against shows was the best way to ensure shows don't accidentally match when processing
         * movies. In testing, some show file names occasionally matched the movie file name regex so
         * this method helps account for the oddball release name.
         */
        val downloadedFiles = Utils.findMediaFiles(storage.completedDownloadsPath, true, null)
        val showsFilterList = downloadedFiles.filter { showSeasonEpBasePattern.matcher(it.fileName.toString()).find() }

        val showPattern = Pattern.compile(Config.properties["regexTvPattern"] as String)
        val moviePattern = Pattern.compile(Config.properties["regexMoviePattern"] as String)

        for (file in downloadedFiles) when (file in showsFilterList) {
            true -> matchAndAddShow(file, showPattern)
            false -> matchAndAddMovie(file, moviePattern)
        }

        shows.map { Show(it).process() }
        movies.map { Movie(it).process() }
    }
}
