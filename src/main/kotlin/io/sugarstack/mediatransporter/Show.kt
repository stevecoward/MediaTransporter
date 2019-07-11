package io.sugarstack.mediatransporter

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Show(val data: ShowData) : Media(data) {

    private lateinit var showRootPath: Path
    lateinit var showSeasonPath: Path

    fun showDirectoryExists(): Boolean {
        showRootPath = Paths.get(Config.tvRootPath.format(Config.mediaShareMount, data.title))
        return Files.isDirectory(showRootPath)
    }

    fun seasonDirectoryExists(): Boolean {
        showSeasonPath = Paths.get(Config.tvSeasonPath.format(Config.mediaShareMount, data.title, data.season))
        return Files.isDirectory(showSeasonPath)
    }
}