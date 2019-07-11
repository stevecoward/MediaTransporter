package io.sugarstack.mediatransporter

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

            val foundFiles = Utils.findMediaFiles(storage.completedDownloadsPath, true)
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
                if (showData.path.toString().contains("S15E01")) {
                    val show = Show(showData)
                    if (!show.showDirectoryExists()) {
//                        TODO
                    }

                    if (!show.seasonDirectoryExists()) {
//                        TODO
                    }

                    if (Utils.findMediaFiles(
                            show.showSeasonPath,
                            false,
                            ".+[sS]${show.data.season}[eE]${show.data.episode}.+"
                        ).count() == 0
                    ) {

//                        TODO: File not found in share, extract from source into destination.
                    } else {
//                        TODO: File was found in share.
                    }

                    val x = ""
                }
            }
        }
    }
}