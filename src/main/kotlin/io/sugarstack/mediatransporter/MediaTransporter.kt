package io.sugarstack.mediatransporter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.sugarstack.mediatransporter.filesystem.Storage
import io.sugarstack.mediatransporter.media.Movie
import io.sugarstack.mediatransporter.media.Show
import io.sugarstack.mediatransporter.media.data.MovieData
import io.sugarstack.mediatransporter.media.data.ShowData
import java.io.File
import java.util.regex.Pattern

class MediaTransporter {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            Cli().main(args)
        }
    }
}

class Cli : CliktCommand() {
    private val propertiesFile: File by option(
        "-p",
        "--properties",
        help = "Absolute path to .properties file"
    ).file().required()

    override fun run() {
        Config(propertiesFile)

        val storage = Storage()
        val shows: MutableList<ShowData> = ArrayList()
        val movies: MutableList<MovieData> = ArrayList()

        storage.determineCapacity()

        val foundFiles = Utils.findMediaFiles(storage.completedDownloadsPath, true, null)
        for (file in foundFiles) {

            val showPattern = Pattern.compile(Config.properties["regexTvPattern"] as String)
            val showsMatches = showPattern.matcher(file.fileName.toString())

            val moviePattern = Pattern.compile(Config.properties["regexMoviePattern"] as String)
            val moviesMatches = moviePattern.matcher(file.fileName.toString())

            while (showsMatches.find()) {
                val parsedShowDetails = ShowData(
                    showsMatches.group("title"),
                    showsMatches.group("season"),
                    showsMatches.group("episode"),
                    file.toAbsolutePath()
                )
                shows.add(parsedShowDetails)
            }

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

        for (showData in shows) {
            val show = Show(showData)
            show.process()
        }

        for (movieData in movies) {
            val movie = Movie(movieData)
            movie.process()
        }
    }

}
