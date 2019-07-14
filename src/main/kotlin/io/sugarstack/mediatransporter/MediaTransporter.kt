package io.sugarstack.mediatransporter

import java.util.regex.Pattern

class MediaTransporter {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val storage = Storage()
            val shows: MutableList<ShowData> = ArrayList()
            val movies: MutableList<MovieData> = ArrayList()

            storage.determineCapacity()

            val foundFiles = Utils.findMediaFiles(storage.completedDownloadsPath, true)
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
                if (!showData.path.toString().contains("sample")) {
                    val show = Show(showData)
                    show.process()
                }
            }

            for (movieData in movies) {
                if (!movieData.path.toString().contains("sample")) {
                    val movie = Movie(movieData)
                    movie.process()
                }
            }
        }
    }
}