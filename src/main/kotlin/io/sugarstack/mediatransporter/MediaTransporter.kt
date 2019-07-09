package io.sugarstack.mediatransporter

import java.lang.Exception

class MediaTransporter {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val storage = Storage()
            val shows: MutableList<ShowData> = ArrayList()
            val movies: MutableList<MovieData> = ArrayList()

            try {
                if(storage.isCapacityReached()) {
                    println("Capacity for storage volume: ${storage.mediaShare} reached. Exiting.")
                    return
                } else {
                    println("Capacity is good, continuing")
                }
            } catch(e: Exception) {
                println("Could not locate the share defined in config!")
                return
            }

            val foundFiles = Utils.findMediaFiles(storage.completedDownloadsPath)
            for (file in foundFiles) {
                println("Found: ${file.toAbsolutePath()}")

                val showsMatches = Config.regexTvPattern.matcher(file.fileName.toString())
                val moviesMatches = Config.regexMoviePattern.matcher(file.fileName.toString())

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
        }
    }
}