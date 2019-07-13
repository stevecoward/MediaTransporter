package io.sugarstack.mediatransporter

class MediaTransporter {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val storage = Storage()
            val shows: MutableList<ShowData> = ArrayList()
            val movies: MutableList<MovieData> = ArrayList()

            storage.determineCapacity()

            val foundFiles = Utils.findMediaFiles(storage.completedDownloadsPath, true)
            for (file in foundFiles) {

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

            for (movieData in movies) {
                val movie = Movie(movieData)
                movie.process()
            }
        }
    }
}