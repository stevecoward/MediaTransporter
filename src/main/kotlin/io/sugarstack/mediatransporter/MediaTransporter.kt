package io.sugarstack.mediatransporter

import java.lang.Exception

class MediaTransporter {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val storage = Storage()
            val shows: MutableList<Map<String, String>> = ArrayList()
            val movies: MutableList<Map<String, String>> = ArrayList()

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
                    val parsedShowDetails = mapOf(
                        "title" to showsMatches.group("title"),
                        "season" to showsMatches.group("season"),
                        "episode" to showsMatches.group("episode"),
                        "path" to file.toAbsolutePath().toString()
                    )
                    shows.add(parsedShowDetails)
                }

                while (moviesMatches.find()) {
                    val parsedMovieDetails = mapOf(
                        "title" to moviesMatches.group("title"),
                        "year" to moviesMatches.group("year"),
                        "resolution" to moviesMatches.group("resolution"),
                        "path" to file.toAbsolutePath().toString()
                    )
                    movies.add(parsedMovieDetails)
                }
            }
        }
    }
}