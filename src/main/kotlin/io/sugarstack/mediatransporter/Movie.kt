package io.sugarstack.mediatransporter

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Movie(private val data: MovieData) : Media(data) {
    private var moviePath = data.sharePath

    override fun toString(): String {
        return "${data.title} (${data.year})"
    }

    fun process() {
        if (filesExist()) {
            logger.info { "Found $this" }
        } else {
            val movieFile = data.path.toFile()

            logger.info { "Copying $this to destination" }

            if (!data.path.toString().endsWith("rar")) {
                movieFile.copyTo(data.sharePath.resolve(data.path.fileName).toFile())
                return
            }

            extract(movieFile, moviePath)
        }
    }

    private fun filesExist(): Boolean {
        return Utils.findMediaFiles(moviePath, false).count() > 0
    }
}