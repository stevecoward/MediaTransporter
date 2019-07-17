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

class MediaTransporter {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            MediaTransporterCli().main(args)
        }
    }
}

class MediaTransporterCli : CliktCommand() {
    private val propertiesFileName: File = File("mediatransporter.properties")
    private val shows: MutableList<ShowData> = ArrayList()
    private val movies: MutableList<MovieData> = ArrayList()
    private val showSeasonEpBasePattern = Pattern.compile("^.+([Ss]\\d{1,}[Ee]\\d{1,})\\..+\$")

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

    override fun run() {
        Config(propertiesFileName)

        val storage = Storage()
        storage.determineCapacity()

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
