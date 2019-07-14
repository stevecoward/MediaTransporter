package io.sugarstack.mediatransporter

import mu.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class Show(private val data: ShowData) : Media(data) {
    private var showSeasonPath = data.sharePath

    override fun toString(): String {
        return "${data.title}: Season ${data.season} Episode ${data.episode}"
    }

    fun process() {
        when (!isSampleFile(data.path)) {
            true -> when (filesExist()) {
                true -> logger.info { "Found $this" } // TODO: If a rar and mkv exist in source, show is "found" twice
                false -> {
                    logger.info { "Copying $this to destination" }
                    val showFile = data.path.toFile()
                    when (!isRarFile(data.path)) {
                        true -> {
                            showFile.copyTo(data.sharePath.resolve(data.path.fileName).toFile())
                            return
                        }
                        else -> extract(showFile, showSeasonPath)
                    }
                }
            }
        }
    }

    private fun isSampleFile(filePath: Path): Boolean = filePath.toString().contains("sample", ignoreCase = true)
    private fun isRarFile(filePath: Path): Boolean = filePath.toString().endsWith("rar")
    private fun filesExist(): Boolean = Utils.findMediaFiles(
        showSeasonPath,
        false,
        ".+[sS]${data.season}[eE]${data.episode}.+"
    ).count() > 0
}