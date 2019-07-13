package io.sugarstack.mediatransporter

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Show(private val data: ShowData) : Media(data) {
    private var showSeasonPath = data.sharePath

    override fun toString(): String {
        return "${data.title}: Season ${data.season} Episode ${data.episode}"
    }

    fun process() {
        if (filesExist()) {
            logger.info { "Found $this" }
        } else {
            val showFile = data.path.toFile()

            logger.info { "Copying $this to destination" }

            if (!data.path.toString().endsWith("rar")) {
                showFile.copyTo(data.sharePath.resolve(data.path.fileName).toFile())
                return
            }

            extract(showFile, showSeasonPath)
        }
    }

    private fun filesExist(): Boolean {
        return Utils.findMediaFiles(
            showSeasonPath,
            false,
            ".+[sS]${data.season}[eE]${data.episode}.+"
        ).count() > 0
    }
}